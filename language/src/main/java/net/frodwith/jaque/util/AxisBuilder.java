package net.frodwith.jaque.util;

import net.frodwith.jaque.data.BigAtom;

public final class AxisBuilder {
  private final boolean tail;
  private final AxisBuilder next;
  private final int length;

  // the true is actually important (see write)
  public static final AxisBuilder EMPTY = new AxisBuilder(true, null, 0);

  private AxisBuilder(boolean tail, AxisBuilder next, int length) {
    this.tail = tail;
    this.next = next;
    this.length = length;
  }

  private AxisBuilder push(boolean tail) {
    return new AxisBuilder(tail, this, length+1);
  }

  public AxisBuilder head() {
    return push(false);
  }

  public AxisBuilder tail() {
    return push(true);
  }

  public Object write() {
    AxisBuilder b = this;
    if ( length < 64 ) {
      long a = 0;
      int i;
      // note <= -- treating EMPTY as a bit
      for ( i = 0; i <= length; ++i, b = b.next ) {
        if ( b.tail ) {
          a |= (1 << i);
        }
      }
      return a;
    }
    else {
      // note incrementing length treats EMPTY as a bit as well
      int bits = length + 1;
      int full = bits >>> 5;
      int left = bits & 31;
      boolean part = 0 != left;
      int bufLen = part ? full + 1 : full;
      int[] words = new int[bufLen];
      for ( int j = 0; j < full; ++j ) {
        int a = 0;
        for ( int i = 0; i < 32; ++i, b = b.next ) {
          if ( b.tail ) {
            a |= (1 << i);
          }
        }
        words[j] = a;
      }
      if ( part ) {
        int a = 0;
        for ( int i = 0; i < left; ++i, b = b.next ) {
          if ( b.tail ) {
            a |= (1 << i);
          }
        }
        words[full] = a;
      }
      return new BigAtom(words);
    }
  }
}
