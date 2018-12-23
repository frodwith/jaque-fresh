package net.frodwith.jaque.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
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
  private final static class JamStream {
    private final OutputStream out;
    private final Map<Object,Object> offsets;

    private byte currentByte;
    private byte bitsInCurrent;
    private Object offset;

    public JamStream(OutputStream out) {
      this.out = out;
      this.currentByte = 0;
      this.bitsInCurrent = 0;
      this.offset = 0L;
      this.offsets = new HashMap<>();
    }

    private void writeBit(boolean bit) throws IOException {
      if ( bit ) {
        currentByte |= (1 << bitsInCurrent);
      }
      if ( 8 == ++bitsInCurrent ) {
        out.write(currentByte);
        currentByte = 0;
        bitsInCurrent = 0;
      }
      offset = HoonMath.increment(offset);
    }

    public void close() throws IOException {
      if ( bitsInCurrent != 0 ) {
        out.write(currentByte);
      }
      currentByte = 0;
      bitsInCurrent = 0;
      out.close();
    }

    private void writeAtom(Object a) throws IOException {
      if ( (a instanceof Long) && (0L == (long) a) ) {
        writeBit(true);
      }
      else {
        int b = HoonMath.met(a),
            c = 32 - Integer.numberOfLeadingZeros(b);

        for ( int i = 0; i < c; i++ ) {
          writeBit(false);
        }
        writeBit(true);

        for ( int i = b; i > 1; i >>= 1 ) {
          writeBit(1 == (i & 1));
        }

        rawAtom(a);
      }
    }

    /* simple version of rawAtom, for debugging
    private void rawAtom(Object atom) throws IOException {
      int atomBits = HoonMath.met(atom);

      for ( int i = 0; i < atomBits; i++ ) {
        writeBit(Atom.getNthBit(atom, i));
      }
    } */

    private void rawAtom(Object atom) throws IOException {
      int atomBits = HoonMath.met(atom);

      // clear currentByte buffer
      if ( bitsInCurrent != 0 ) {
        int i = 0;
        while ( bitsInCurrent != 0 && i < atomBits ) {
          writeBit(Atom.getNthBit(atom, i++));
        }
        if ( i == atomBits ) {
          return;
        }
        else {
          atom = HoonMath.rsh((byte) 0, i, atom);
        }
      }

      // write whole bytes
      final byte[] rest = Atom.toByteArray(atom);
      int whole = rest.length - 1;
      for (int i = 0; i < whole; i++) {
        out.write(rest[i]);
      }
      offset = HoonMath.add(offset, ((long) whole) << 3);

      // keep what's left of the last byte
      byte last = rest[whole];
      int lastBits = 32 - Integer.numberOfLeadingZeros(0xff & last);
      if ( 8 == lastBits ) {
        out.write(last);
        currentByte = 0;
        bitsInCurrent = 0;
      }
      else {
        currentByte = last;
        bitsInCurrent = (byte) lastBits;
      }
      offset = HoonMath.add(offset, (long) lastBits);
    }

    @TruffleBoundary
    public void writeNoun(Object noun) throws IOException {
      ArrayDeque<Object> stack = new ArrayDeque<>();
      stack.push(noun);
      do {
        Object top = stack.pop();
        Object done = offsets.get(top);
        if ( null == done ) {
          offsets.put(top, offset);
          if ( top instanceof Cell ) {
            Cell c = (Cell) top;
            writeBit(true);
            writeBit(false);
            stack.push(c.tail);
            stack.push(c.head);
          }
          else {
            writeBit(false);
            writeAtom(top);
          }
        }
        else if ( top instanceof Cell ) {
          writeBit(true);
          writeBit(true);
          writeAtom(done);
        }
        else if ( HoonMath.met(top) <= HoonMath.met(done) ) {
          writeBit(false);
          writeAtom(top);
        }
        else {
          writeBit(true);
          writeBit(true);
          writeAtom(done);
        }
      } while (!stack.isEmpty());
    }
  }

  public static void jamStream(OutputStream out, Object noun) throws IOException {
    JamStream s = new JamStream(out);
    s.writeNoun(noun);
    s.close();
  }

  public static byte[] jamBytes(Object noun) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      jamStream(out, noun);
      return out.toByteArray();
    }
    catch ( IOException e ) {
      throw new FailError(e.getMessage());
    }
  }

  public static Object jam(Object noun) {
    return Atom.fromByteArray(jamBytes(noun));
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
