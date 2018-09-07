package net.frodwith.jaque.runtime;

import java.util.Arrays;

import net.frodwith.jaque.exception.AtomRequiredException;

import net.frodwith.jaque.data.BigAtom;

public final class Atom {

  public static Object require(Object o) throws AtomRequiredException {
    if ( o instanceof Long || o instanceof BigAtom ) {
      return o;
    }
    else {
      throw new AtomRequiredException(o);
    }
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
}
