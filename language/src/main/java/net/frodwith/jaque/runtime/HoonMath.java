package net.frodwith.jaque.runtime;

public final class HoonMath {
  public static int met(byte bloq, Object atom) {
    int gal, daz;

    if ( atom instanceof Long ) {
      long v = (long) atom;
      if ( 0 == v ) {
        return 0;
      }
      else {
        int left = (int) (v >>> 32);
        if ( left == 0 ) {
          gal = 0;
          daz = (int) v;
        }
        else {
          gal = 1;
          daz = left; 
        }
      }
    }
    else {
      int[] w = (int[]) atom;
      gal = w.length - 1;
      daz = w[gal];
    }
    
    switch (bloq) {
      case 0:
      case 1:
      case 2:
        int col = 32 - Integer.numberOfLeadingZeros(daz),
            bif = col + (gal << 5);

        return (bif + ((1 << bloq) - 1) >>> bloq);

      case 3:
        return (gal << 2)
          + ((daz >>> 24 != 0)
            ? 4
            : (daz >>> 16 != 0)
            ? 3
            : (daz >>> 8 != 0)
            ? 2
            : 1);

      case 4:
        return (gal << 1) + ((daz >>> 16 != 0) ? 2 : 1);

      default: {
        int gow = bloq - 5;
        return ((gal + 1) + ((1 << gow) - 1)) >>> gow;
      }
    }
  }
  
  public static int met(Object atom) {
    return met((byte)0, atom);
  }

  public static Object lsh(byte bloq, int count, Object atom) {
    int len = met(bloq, atom),
        big;

    if ( 0 == len ) {
      return 0L;
    }
    try {
      big = Math.addExact(count, len);
    }
    catch (ArithmeticException e) {
      assert(false);
    }
    
    int[] sal = Atom.slaq(bloq, big);
    Atom.chop(bloq, 0, len, count, sal, atom);

    return Atom.malt(sal);
  }

  public static Object addWords(int[] a, int[] b) {
    MPNSquare s = new MPNSquare(a, b);
    int[] dst   = new int[s.len+1];
    dst[s.len]  = MPN.add_n(dst, s.x, s.y, s.len);
    return Atom.malt(dst);
  }
  
  public static long add(long a, long b) throws ArithmeticException {
    long c = a + b;
    if ( Long.compareUnsigned(c, a) < 0 ||
         Long.compareUnsigned(c, b) < 0 ) {
      throw new ArithmeticException();
    }
    return c;
  }
  
 public static Object add(Object a, Object b) {
    if ( a instanceof Long && b instanceof Long ) {
      try {
        return add((long) a, (long) b);
      }
      catch (ArithmeticException e) {
      }
    }
    return add(Atom.words(a), Atom.words(b));
  }


  public static Object subtractWords(int[] a, int[] b) {
    MPNSquare s = new MPNSquare(a, b);
    int[] dst = new int[s.len];
    int bor = MPN.sub_n(dst, s.x, s.y, s.len);
    if ( bor != 0 ) {
      throw new Bail();
    }
    return Atom.malt(dst);
  }

  public static long sub(long a, long b) {
    if ( -1 == Long.compareUnsigned(a, b) ) {
      throw new Bail();
    }
    else {
      return a - b;
    }
  }

  public static Object sub(BigAtom a, BigAtom b) {
    return subtractWords(a.words, b.words);
  }
  
  public static Object sub(Object a, Object b) {
    return ( a instanceof Long && b instanceof Long )
      ? sub((long) a, (long) b)
      : subtractWords(Atom.words(a), Atom.words(b));
  }

  public static Object peg(Object axis, Object to) {
    if ( 1L == to ) {
      return axis;
    }
    else {
      int c = met(to),
          d = c - 1;

      Object e = lsh((byte) 0, d, 1L),
             f = sub(to, e),
             g = lsh((byte) 0, d, axis);
      
      return add(f, g);
    }
  }
}
