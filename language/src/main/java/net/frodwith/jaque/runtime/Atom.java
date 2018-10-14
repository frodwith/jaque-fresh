package net.frodwith.jaque.runtime;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import gnu.math.MPN;

import net.frodwith.jaque.exception.AtomRequiredException;
import net.frodwith.jaque.exception.IntRequiredException;

import net.frodwith.jaque.data.BigAtom;

public final class Atom {

	public static int compare(BigAtom a, BigAtom b) {
    return MPN.cmp(a.words, a.words.length, b.words, b.words.length);
  }
  
  public static int compare(long a, long b) {
    return Long.compareUnsigned(a, b);
  }
  
  // -1, 0, 1 for less than, equal, or greater than respectively
  public static int compare(Object a, Object b) {
    if ( a instanceof Long ) {
      if ( b instanceof Long ) {
        return compare((long) a, (long) b);
      }
      else {
        return -1;
      }
    }
    else if ( b instanceof Long ) {
      return 1;
    }
    else {
      return compare((BigAtom) a, (BigAtom) b);
    }
  }

  public static Object require(Object o) throws AtomRequiredException {
    if ( o instanceof Long || o instanceof BigAtom ) {
      return o;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new AtomRequiredException(o);
    }
  }

  public static int requireInt(Object o) throws IntRequiredException {
    if ( o instanceof Long ) {
      long atom = (long) o;
      if ( 1 != Long.compareUnsigned(atom, 0xFFFFFFFF) ) {
        return (int) atom;
      }
    }
    CompilerDirectives.transferToInterpreter();
    throw new IntRequiredException(o);
  }

  public static int[] words(long l) {
    int low  = (int) l,
        high = (int) (l >>> 32);

    return ( high == 0 )
      ? new int[] { low }
      : new int[] { low, high };
  }

  public static int[] words(BigAtom a) {
    return a.words;
  }

  public static int[] words(Object o) {
    return ( o instanceof BigAtom ) 
      ? words((BigAtom) o)
      : words((long) o);
  }

  public static Object malt(int[] words) {
    int bad = 0;

    for ( int i = words.length - 1; i >= 0; --i) {
      if ( words[i] == 0 ) {
        ++bad;
      }
      else {
        break;
      }
    }

    if ( bad > 0 ) {
      words = Arrays.copyOfRange(words, 0, words.length - bad);
    }

    if ( 0 == words.length ) {
      return 0L;
    }
    else if ( words != null && words.length > 2 ) {
      return new BigAtom(words);
    }
    else if (words.length == 1) {
      return words[0] & 0xffffffffL;
    }
    else {
      return ((words[1] & 0xffffffffL) << 32) | (words[0] & 0xffffffffL);
    }
  }
  
  public static int[] slaq(byte bloq, int len) {
    int big = ((len << bloq) + 31) >>> 5;
    return new int[big];
  }

  public static void chop(byte met, int fum, int wid, int tou, int[] dst, Object src) {
    int[] buf = words(src);
    int   len = buf.length, i;

    if (met < 5) {
      int san = 1 << met,
      mek = ((1 << san) - 1),
      baf = fum << met,
      bat = tou << met;

      for (i = 0; i < wid; ++i) {
        int waf = baf >>> 5,
            raf = baf & 31,
            wat = bat >>> 5,
            rat = bat & 31,
            hop;

        hop = (waf >= len) ? 0 : buf[waf];
        hop = (hop >>> raf) & mek;
        dst[wat] ^= hop << rat;
        baf += san;
        bat += san;
      }
    }
    else {
      int hut = met - 5,
          san = 1 << hut,
          j;

      for (i = 0; i < wid; ++i) {
        int wuf = (fum + i) << hut,
            wut = (tou + i) << hut;

        for (j = 0; j < san; ++j) {
          dst[wut + j] ^= ((wuf + j) >= len)
                       ? 0
                       : buf[wuf + j];
        }
      }
    }
  }

  public static boolean getNthBit(long atom, int n) {
    if ( n >= (Long.SIZE - 1) ) {
      return false;
    }
    else {
      return ((atom & (1L << n)) != 0);
    }
  }
  
  public static boolean getNthBit(Object atom, int n) {
    if ( atom instanceof Long ) {
      return getNthBit((long) atom, n);
    }
    else {
      return getNthBit((BigAtom) atom, n);
    }
  }

  public static boolean getNthBit(BigAtom atom, int n) {
    int pix = n >> 5;
    
    if ( pix >= atom.words.length ) {
      return false;
    }
    else {
      return (1 & (atom.words[pix] >>> (n & 31))) != 0;
    }
  }
}
