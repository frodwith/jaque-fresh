package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.FailError;

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
      int bel = bits >>> 5;
      if ( bits != bel << 5 ) {
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
}
