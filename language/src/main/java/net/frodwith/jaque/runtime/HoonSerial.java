package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.FailError;
import net.frodwith.jaque.exception.ExitException;

public final class HoonSerial {
  private final static class JamBuffer {
    public int a, b, bits;
    public int[] words;

    public JamBuffer() {
      this.a     = 89;  // fib(11)
      this.b     = 144; // fib(12)
      this.bits  = 0;
      this.words = new int[5];
    }

    public int byteLength() {
      int bel = bits >>> 3;
      if ( bits != bel << 3 ) {
        bel++;
      }
      return bel;
    }

    public Object toAtom() {
      return Atom.malt(words);
    }

    public byte[] toByteArray() {
      return Atom.wordsToBytes(words, byteLength());
    }

    public void grow(int mor) {
      int want = bits + mor;
      if ( want < mor ) {
        // overflow
        throw new FailError("jam bit count overflow");
      }
      if ( want > a ) {
        int old = a >>> 5, c = 0, big;
        if ( old << 5 != a ) {
          ++old;
        }
        // fibbonaci growth
        while ( c < want ) {
          c = a + b;
          b = a;
          a = c;
        }
        big = c >>> 5;
        if ( (big << 5) != c ) {
          big++;
        }
        int[] olds = words;
        words = new int[big];
        System.arraycopy(olds, 0, words, 0, olds.length);
      }
    }

    public void chop(int met, Object atom) {
      grow(met);
      Atom.chop((byte) 0, 0, met, bits, words, atom);
      bits += met;
    }

    public void atom(Object a) {
			if ( (a instanceof Long) && (0L == (long) a) ) {
				chop(1, 1L);
			}
			else {
        int b = HoonMath.met(a),
            c = 32 - Integer.numberOfLeadingZeros(b);
        chop(c+1, 1L << c);
        chop(c-1, (long) (b & ((1 << (c-1)) - 1)));
        chop(b, a);
			}
    }
  }

  @TruffleBoundary
  private static void jamBuf(Object noun, JamBuffer buf) {
    ArrayDeque<Object> stack = new ArrayDeque<>();
    Map<Object,Long> offsets = new HashMap<>();
    stack.push(noun);
    do {
      Object top = stack.pop();
      Long offset = offsets.get(top);
      if ( null == offset ) {
        offsets.put(top, (long) buf.bits);
        if ( top instanceof Cell ) {
          Cell c = (Cell) top;
          buf.chop(2, 1L);
          stack.push(c.tail);
          stack.push(c.head);
        }
        else {
          buf.chop(1, 0L);
          buf.atom(top);
        }
      }
      else if ( top instanceof Cell ) {
        buf.chop(2, 3L);
        buf.atom(offset);
      }
      else {
        if ( HoonMath.met(top) <= HoonMath.met(offset) ) {
          buf.chop(1, 0L);
          buf.atom(top);
        }
        else {
          buf.chop(2, 3L);
          buf.atom(offset);
        }
      }
    } while ( !stack.isEmpty() );
  }

  public static byte[] jamBytes(Object noun) {
    JamBuffer buf = new JamBuffer();
    jamBuf(noun, buf);
    return buf.toByteArray();
  }

  public static Object jam(Object noun) {
    JamBuffer buf = new JamBuffer();
    jamBuf(noun, buf);
    return buf.toAtom();
  }

  public static Cell rub(Object a, Object b) throws ExitException {
    Object c, d, e, w, x, y, z, p, q, m;

    m = HoonMath.add(a, (long) HoonMath.met(b));
    x = a;

    while ( Atom.isZero(HoonMath.cut((byte)0, x, 1L, b)) ) {
      y = HoonMath.increment(x);
      
      //  Sanity check: crash if decoding more bits than available
      if ( Atom.compare(x, m) > 0 ) {
        throw new ExitException("rub-decode-bits");
      }

      x = y;
    }

    if ( Equality.equals(x, a) ) {
      return new Cell(1L, 0L);
    }
    c = HoonMath.sub(x, a);
    d = HoonMath.increment(x);

    x = HoonMath.dec(c);
    y = HoonMath.bex(Atom.requireLong(x));
    z = HoonMath.cut((byte)0, d, x, b);

    e = HoonMath.add(y, z);
    w = HoonMath.add(c, c);
    y = HoonMath.add(w, e);
    z = HoonMath.add(d, x);

    p = HoonMath.add(w, e);
    q = HoonMath.cut((byte)0, z, e, b);
    
    return new Cell(p, q);
  }

  private static Cell cue(Map<Object,Object> m, Object a, Object b) throws ExitException {
    Object p, q;

    if ( Atom.isZero(HoonMath.cut((byte) 0, b, 1L, a)) ) {
      Object x = HoonMath.increment(b);
      Cell   c = rub(x, a);

      p = HoonMath.increment(c.head);
      q = c.tail;
      m.put(b, q);
    }
    else {
      Object c = HoonMath.add(2L, b),
             l = HoonMath.increment(b);

      if ( Atom.isZero(HoonMath.cut((byte) 0, l, 1L, a)) ) {
        Cell u, v;
        Object w, x, y;

        u = cue(m, a, c);
        x = HoonMath.add(u.head, c);
        v = cue(m, a, x);
        w = new Cell(
            Cell.require(u.tail).head,
            Cell.require(v.tail).head);
        y = HoonMath.add(u.head, v.head);
        p = HoonMath.add(2L, y);
        q = w;
        m.put(b, q);
      }
      else {
        Cell d = rub(c, a);
        Object x = m.get(d.tail);

        if ( null == x ) {
          throw new ExitException("cue-bad-pointer");
        }

        p = HoonMath.add(2L, d.head);
        q = x;
      }
    }
    return new Cell(p, new Cell(q, 0L));
  }

  @TruffleBoundary
  public static Object cue(Object a) throws ExitException {
    Cell x = cue(new HashMap<>(), a, 0L);
    return Cell.require(x.tail).head;
  }
}
