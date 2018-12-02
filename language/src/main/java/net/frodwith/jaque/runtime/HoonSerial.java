package net.frodwith.jaque.runtime;

public final class HoonSerial {
  private final static class JamBuffer {
    public int a, b, bits;
    public int[] words;

    public JamBuffer() {
      this.a    = 89L;  // fib(11)
      this.b    = 144L; // fib(12)
      this.bits = 0L;
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

    public void grow(int bits) {
      int want = this.bits + bits;
      if ( want < bits ) {
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

    public void chop(int bits, Object atom) {
      grow(bits);
      Atom.chop(0, 0, bits, words, atom);
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
        offsets.put(top, (long) out.size());
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
        if ( Atom.met(top) <= Atom.met(offset) ) {
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
