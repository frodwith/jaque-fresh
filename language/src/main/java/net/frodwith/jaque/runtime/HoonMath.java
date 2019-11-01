package net.frodwith.jaque.runtime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import gnu.math.MPN;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.FailError;

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
      int[] w = ((BigAtom) atom).words;
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
      return BigAtom.MINIMUM;
    }
  }

  public static BigAtom increment(BigAtom atom) {
    final int[] words = Arrays.copyOf(atom.words, atom.words.length);
    Atom.incrementInPlace(words);
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
  
  public static long addLongs(long a, long b) throws ArithmeticException {
    // Do not use Math.addExact here: gives bad results for signed longs
    long c = a + b;
    if ( Long.compareUnsigned(c, a) >= 0 ) {
      return c;
    }
    else {
      throw new ArithmeticException();
    }
  }

  public static Object add(long a, long b) {
    try {
      return addLongs(a, b);
    }
    catch (ArithmeticException e) {
      return addWords(Atom.words(a), Atom.words(b));
    }
  }

  public static Object add(Object a, Object b) {
    if ( a instanceof Long && b instanceof Long ) {
      return add((long) a, (long) b);
    }
    return addWords(Atom.words(a), Atom.words(b));
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
    int[] w = atom.words;
    w = Arrays.copyOf(w, w.length);
    w = Atom.decrementInPlace(w);
    return ( 2 == w.length )
      ? Atom.wordsToLong(w)
      : new BigAtom(w);
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
    switch ( Long.compareUnsigned(a, b) ) {
      case 0:
        return 0L;
      case 1:
        return a - b;
      default:
        throw new ExitException("subtract underflow");
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

  public static long div(long a, long b) throws ExitException {
    if ( 0L == b ) {
      throw new ExitException("divide by zero");
    }
    else {
      return Long.divideUnsigned(a, b);
    }
  }

  /* This code is substantially adapted from Kawa's IntNum.java -- see the note at
   * the top of gnu.math.MPN */
  private static Cell divmod(int[] x, int[] y) {
    int xlen = x.length,
        ylen = y.length,
        rlen, qlen;
    int[] xwords = Arrays.copyOf(x, xlen+2),
          ywords = Arrays.copyOf(y, ylen);

    int nshift = MPN.count_leading_zeros(ywords[ylen-1]);
    if (nshift != 0) {
      MPN.lshift(ywords, 0, ywords, ylen, nshift);
      int x_high = MPN.lshift(xwords, 0, xwords, xlen, nshift);
      xwords[xlen++] = x_high;
    }

    if (xlen == ylen) {
      xwords[xlen++] = 0;
    }

    MPN.divide(xwords, xlen, ywords, ylen);
    rlen = ylen;
    MPN.rshift0(ywords, xwords, 0, rlen, nshift);
    qlen = xlen + 1 - ylen;
    xwords = Arrays.copyOfRange(xwords, ylen, ylen+qlen);
    while ( rlen > 1 && 0 == ywords[rlen - 1] ) {
      --rlen;
    }
    if ( ywords[rlen-1] < 0 ) {
      ywords[rlen++] = 0;
    }

    return new Cell(Atom.malt(xwords), Atom.malt(ywords));
  }

  // NO ZERO CHECK
  private static Object div(int[] x, int[] y) {
    int cmp = Atom.compareWords(x, y);
    if ( cmp < 0 ) {
      return 0L;
    }
    else if ( 0 == cmp ) {
      return 1L;
    }
    else if ( 1 == y.length ) {
      int[] q = new int[x.length];
      MPN.divmod_1(q, x, x.length, y[0]);
      return Atom.malt(q);
    }
    else {
      return divmod(x,y).head;
    }
  }

  public static Object div(Object a, Object b) throws ExitException {
    if ( Atom.isZero(b) ) {
      throw new ExitException("divide by zero");
    }
    else if ( (a instanceof Long) && (b instanceof Long) ) {
      return div((long) a, (long) b);
    }
    else {
      return div(Atom.words(a), Atom.words(b));
    }
  }

  // throws on divide by zero
  public static long mod(long a, long b) throws ArithmeticException {
    return Long.remainderUnsigned(a, b);
  }

  // NO ZERO CHECK
  public static Object mod(int[] x, int[] y) {
    int cmp = Atom.compareWords(x, y);
    if ( cmp < 0 ) {
      return y;
    }
    else if ( 0 == cmp ) {
      return 0L;
    }
    else if ( 1 == y.length ) {
      int[] q = new int[x.length];
      return (long) MPN.divmod_1(q, x, x.length, y[0]);
    }
    else {
      return divmod(x,y).tail;
    }
  }

  public static Object mod(Object a, Object b) throws ExitException {
    if ( Atom.isZero(b) ) {
      throw new ExitException("mod by zero");
    }
    else if ( (a instanceof Long) && (b instanceof Long) ) {
      return mod((long) a, (long) b);
    }
    else {
      return mod(Atom.words(a), Atom.words(b));
    }
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

  public static long mulLongs(long a, long b) throws ArithmeticException {
    if ( (a < 0L) || (b < 0L) ) {
      // multiplyExact would get cute with negative numbers
      throw new ArithmeticException();
    }
    else {
      return Math.multiplyExact(a, b);
    }
  }

  public static Object mul(long a, long b) {
    try {
      return mulLongs(a, b);
    }
    catch (ArithmeticException e) {
      return mul(Atom.words(a), Atom.words(b));
    }
  }
 
  private static Object mul(int[] x, int[] y) {
    int xlen = x.length,
        ylen = y.length;
    int[] dest = new int[xlen + ylen];

    if ( xlen < ylen ) {
      int zlen = xlen;
      int[] z = x;

      x = y;
      y = z;
      xlen = ylen;
      ylen = zlen;
    }

    MPN.mul(dest, x, xlen, y, ylen);
    return Atom.malt(dest);
  }

  public static Object mul(Object a, Object b) {
    if ( (a instanceof Long) && (b instanceof Long) ) {
      try {
        return mul((long) a, (long) b);
      }
      catch (ArithmeticException e) {
      }
    }
    return mul(Atom.words(a), Atom.words(b));
  } 

  public static Object bex(long a) {
    if (a < 64) {
      return 1L << a;
    }
    else {
      int whole = (int) (a >> 5),
          parts = (int) a & 31;

      int[] words = new int[whole+1];
      words[whole] = 1 << parts;
      return words;
    }
  }

  public static long con(long a, long b) {
    return a | b;
  }

  public static Object con(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = Atom.words(b);

      Atom.chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < lnb; i++ ) {
        sal[i] |= bow[i];
      }

      return Atom.malt(sal);
    }
  }

  public static long cap(Object atom) throws ExitException {
    int b = met(atom);
    if ( b < 2 ) {
      throw new ExitException("cap b < 2");
    }
    return Atom.getNthBit(atom, b - 2) ? 3 : 2;
  }

  public static long mas(long atom) {
    int  b = 64 - Long.numberOfLeadingZeros(atom);
    long c = 1 << (b - 1),
         d = 1 << (b - 2),
         e = atom - c;
    return e | d;
  }

  public static Object mas(BigAtom atom) {
    int    b = met(atom);
    Object c = bex(b - 1),
           d = bex(b - 2),
           e;
    try {
      e = sub(atom, c);
    }
    catch ( ExitException ex ) {
      throw new AssertionError();
    }
    return con(e, d);
  }

  public static Object mas(Object atom) {
    return ( atom instanceof Long )
      ? mas((long) atom)
      : mas((BigAtom) atom);
  }

  public static Object cat(byte a, Object b, Object c)
      throws ExitException {
    int lew = met(a, b),
        ler = met(a, c),
        all = lew + ler;

    if ( 0 == all ) {
      return 0L;
    }
    else {
      int[] sal = Atom.slaq(a, all);

      Atom.chop(a, 0, lew, 0, sal, b);
      Atom.chop(a, 0, ler, lew, sal, c);

      return Atom.malt(sal);
    }
  }

  public static Object
    cut(byte a, Object b, Object c, Object d)
      throws ExitException {
    int ci, bi;
    try {
      bi = Atom.requireInt(b);
      ci = Atom.requireInt(c);
    }
    catch ( ExitException e ) {
      throw new FailError("cut too large");
    }
    int len = met(a, d);

    if ( (0 == ci) || (bi >= len) ) {
      return 0L;
    }

    if ( (bi + ci) > len ) {
      ci = len - bi;
    }

    if ( (bi == 0) && (ci == len) ) {
      return d;
    }
    else {
      int[] sal = Atom.slaq(a, ci);
      Atom.chop(a,  bi, ci, 0, sal, d);
      return Atom.malt(sal);
    }
  }

  public static Object end(byte a, Object b, Object c) {
    int bi;

    try {
      bi = Atom.requireInt(b);
    }
    catch ( ExitException e ) {
      throw new FailError("end too large");
    }
    if ( 0 == bi ) {
      return 0L;
    }

    int len = met(a, c);
    if ( bi >= len ) {
      return c;
    }

    int[] sal = Atom.slaq(a, bi);
    Atom.chop(a, 0, bi, 0, sal, c);
    return Atom.malt(sal);
  }

  public static long dis(long a, long b) {
    return a & b;
  }

  public static Object dis(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = Atom.words(b);

      Atom.chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < len; i++ ) {
        sal[i] &= (i >= lnb) ? 0 : bow[i];
      }

      return Atom.malt(sal);
    }
  }

  public static long mix(long a, long b) {
    return a ^ b;
  }

  public static Object mix(Object a, Object b) {
    byte w   = 5;
    int  lna = met(w, a);
    int  lnb = met(w, b);

    if ( (0 == lna) && (0 == lnb) ) {
      return 0L;
    }
    else {
      int i, len = Math.max(lna, lnb);
      int[] sal  = new int[len];
      int[] bow  = Atom.words(b);

      Atom.chop(w, 0, lna, 0, sal, a);

      for ( i = 0; i < lnb; i++ ) {
        sal[i] ^= bow[i];
      }

      return Atom.malt(sal);
    }
  }

  @TruffleBoundary
  private static byte[] doSha(String algo, byte[] bytes) throws ExitException {
    try {
      return MessageDigest.getInstance(algo).digest(bytes);
    }
    catch (NoSuchAlgorithmException e) {
      throw new ExitException("No such sha algorithm " + algo);
    }
  }

  private static Object sha_help(Object len, Object atom, String algo)
      throws ExitException {
    int leni = Atom.requireInt(len);
    byte[] in = Atom.toByteArray(atom);
    return Atom.fromByteArray(doSha(algo, in));
  }

  public static Object shal(Object len, Object atom) throws ExitException {
    return sha_help(len, atom, "SHA-512");
  }

  public static Object shan(Object atom) throws ExitException {
    byte[] in = Atom.toByteArray(atom);
    return Atom.fromByteArray(doSha("SHA-1", in), Atom.BIG_ENDIAN);
  }
}
