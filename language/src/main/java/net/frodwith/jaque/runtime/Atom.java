package net.frodwith.jaque.runtime;

import java.util.Arrays;

import net.frodwith.jaque.exception.AtomRequiredException;

import net.frodwith.jaque.data.BigAtom;

public final class Atom {
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

  public static Object require(Object o) throws AtomRequiredException {
    if ( o instanceof Long || o instanceof BigAtom ) {
      return o;
    }
    else {
      throw new AtomRequiredException(o);
    }
  }
}
