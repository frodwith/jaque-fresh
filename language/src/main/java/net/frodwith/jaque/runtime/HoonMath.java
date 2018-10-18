package net.frodwith.jaque.runtime;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import gnu.math.MPN;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.FailError;

public final class HoonMath {
  public static final BigAtom MINIMUM_BIGATOM = new BigAtom(new int[] {0, 0, 1});

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

  // lsh fails because we don't really have infinitely sized atoms
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
      CompilerDirectives.transferToInterpreter();
      throw new FailError("slaq count doesn't fit in int");
    }
    
    int[] sal = Atom.slaq(bloq, big);
    Atom.chop(bloq, 0, len, count, sal, atom);

    return Atom.malt(sal);
  }

  public static Object rsh(byte bloq, int count, Object atom) {
    int len = met(bloq, atom);

    if ( count >= len ) {
      return 0L;
    }
    else {
      int[] sal = Atom.slaq(bloq, len - count);

      Atom.chop(bloq, count, len - count, 0, sal, atom);

      return Atom.malt(sal);
    }
  }

  private static int[] incrementInPlace(int[] vol) {
    for ( int i = 0; i < vol.length; i++ ) {
      if ( 0 != ++vol[i] ) {
        return vol;
      }
    }
    int[] bigger = new int[vol.length + 1];
    bigger[bigger.length] = 1;
    return bigger;
  }

  public static long unsignedIncrementExact(long atom) throws ArithmeticException {
    if ( 0L == ++atom ) {
      throw new ArithmeticException();
    }
    return atom;
  }
  
  public static Object increment(long atom) {
    try {
      return unsignedIncrementExact((long) atom);
    } 
    catch (ArithmeticException e) {
      return MINIMUM_BIGATOM;
    }
  }

  public static BigAtom increment(BigAtom atom) {
    final int[] words = Arrays.copyOf(atom.words, atom.words.length);
    incrementInPlace(words);
    return new BigAtom(words);
  }
  
  public static Object increment(Object atom) {
    return ( atom instanceof Long )
      ? increment((long) atom)
      : increment((BigAtom) atom);
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

  public static long dec(long atom) throws ExitException {
    if ( atom == 0 ) {
      throw new ExitException("decrement underflow");
    }
    else {
      return atom - 1;
    }
  }

  public static Object dec(BigAtom atom) {
    int[] result;
    if ( atom.words[0] == 0 ) {
      result = new int[atom.words.length - 1];
      Arrays.fill(result, 0xFFFFFFFF);
    }
    else {
      result = Arrays.copyOf(atom.words, atom.words.length);
      result[0] -= 1;
    }

    return Atom.malt(result);
  }

  public static Object dec(Object atom) throws ExitException {
    if ( atom instanceof Long ) {
      return dec((long) atom);
    }
    else {
      return dec((BigAtom) atom);
    }
  }


  public static Object subtractWords(int[] a, int[] b) throws ExitException {
    MPNSquare s = new MPNSquare(a, b);
    int[] dst = new int[s.len];
    int bor = MPN.sub_n(dst, s.x, s.y, s.len);
    if ( bor != 0 ) {
      CompilerDirectives.transferToInterpreter();
      throw new ExitException("subtract underflow");
    }
    return Atom.malt(dst);
  }

  public static long sub(long a, long b) throws ExitException {
    if ( -1 == Long.compareUnsigned(a, b) ) {
      throw new ExitException("subtract underflow");
    }
    else {
      return a - b;
    }
  }

  public static Object sub(BigAtom a, BigAtom b) throws ExitException {
    return subtractWords(a.words, b.words);
  }
  
  public static Object sub(Object a, Object b) throws ExitException {
    return ( a instanceof Long && b instanceof Long )
      ? sub((long) a, (long) b)
      : subtractWords(Atom.words(a), Atom.words(b));
  }

  public static Object peg(Object axis, Object to) {
    if ( (to instanceof Long) && (1L == (long) to) ) {
      return axis;
    }
    else {
      int c = met(to),
          d = c - 1;

      Object e = lsh((byte) 0, d, 1L), f,
             g = lsh((byte) 0, d, axis);

      try {
        f = sub(to, e);
      }
      catch ( ExitException ex ) {
        CompilerDirectives.transferToInterpreter();
        throw new AssertionError();
      }

      
      return add(f, g);
    }
  }
}
