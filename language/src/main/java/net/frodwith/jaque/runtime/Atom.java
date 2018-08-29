package net.frodwith.jaque.runtime;

import net.frodwith.jaque.data.BigAtom;

public class Atom {
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
}
