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

  private enum CueFrameType { CUE_HEAD, CUE_TAIL };

  @TruffleBoundary
  public static Object cue(Object a) throws ExitException {
    final class Frame {
      public CueFrameType type;
      public Object cur;
      public Object wid;
      public Object hed;

      public Frame(CueFrameType type, Object cur, Object wid, Object hed) {
        this.type = type;
        this.cur  = cur;
        this.wid  = wid;
        this.hed  = hed;
      }
    }

    ArrayDeque<Frame> stack = new ArrayDeque<>();
    Map<Object,Object>    m = new HashMap<>();
    Object cur = 0L;
    Object wid, pro;

    //  read from atom at cursor
    //
    advance:
    while ( true ) {
      long tag = (long)HoonMath.cut((byte) 0, cur, 1L, a);

      //  low bit unset, (1 + cur) points to an atom
      //
      if ( 0L == tag ) {
        Cell bur = rub(HoonMath.increment(cur), a);
        pro = bur.tail;
        wid = HoonMath.increment(bur.head);
        m.put(cur, pro);
        // goto retreat
      }
      else {
        tag = (long)HoonMath.cut((byte) 0, HoonMath.increment(cur), 1L, a);

        //  next bit set, (2 + cur) points to a backref
        //
        if ( 1L == tag ) {
          Cell bur = rub(HoonMath.add(2L, cur), a);
          pro = m.get(bur.tail);

          if ( null == pro ) {
            throw new ExitException("cue-bad-pointer");
          }

          wid = HoonMath.add(2L, bur.head);
          // goto retreat
        }
        //  next bit unset, (2 + cur) points to the head of a cell
        //
        else {
          stack.push(new Frame(CueFrameType.CUE_HEAD, cur, null, null));
          cur = HoonMath.add(2L, cur);
          continue advance;
        }
      }

      //  consume: popped stack frame, .wid and .pro from above.
      //
      retreat:
      while ( true ) {
        if ( stack.isEmpty() ) {
          return pro;
        }
        else {
          Frame top = stack.pop();

          switch ( top.type ) {
            //  XX default panic?

            //  .wid and .pro are the head of the cell at top.cur.
            //  save them (and the cell cursor) in a TAIL frame,
            //  set the cursor to the tail and read there.
            //
            case CUE_HEAD: {
              stack.push(new Frame(CueFrameType.  CUE_TAIL, top.cur, wid, pro));
              cur = HoonMath.add(2L, HoonMath.add(wid, top.cur));
              continue advance;
            }

            //  .wid and .pro are the tail of the cell at top.cur,
            //  construct the cell, memoize it, and produce it along with
            //  its total width (as if it were a read from above).
            //
            case CUE_TAIL: {
              pro = new Cell(top.hed, pro);
              m.put(top.cur, pro);
              wid = HoonMath.add(2L, HoonMath.add(wid, top.wid));
              continue retreat;
            }
          }
        }
      }
    }
  }
}
