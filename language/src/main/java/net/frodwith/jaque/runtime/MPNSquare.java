package net.frodwith.jaque.runtime;

// words arrays of equal size for use with MPN functions
public final class MPNSquare {
  int[] x;
  int[] y;
  int   len;

  public MPNSquare(int[] aw, int[] bw) {
    int as = aw.length, bs = bw.length;
    if (as > bs) {
      len = as;
      x   = aw;
      y   = new int[len];
      System.arraycopy(bw, 0, y, 0, bs);
    }
    else if (as < bs) {
      len = bs;
      x   = new int[len];
      y   = bw;
      System.arraycopy(aw, 0, x, 0, as);
    }
    else {
      len = as;
      x   = aw;
      y   = bw;
    }
  }
}
