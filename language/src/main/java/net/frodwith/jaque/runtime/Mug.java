package net.frodwith.jaque.runtime;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;

import net.frodwith.jaque.runtime.Atom;

public final class Mug {
  private static final int FNV_START_WORDS = 0x811c9dc5;

  public static int get(Cell c) {
    return c.hashCode();
  }
  
  public static int get(BigAtom a) {
    return a.hashCode();
  }

  public static int get(long l) {
    int[] w = Atom.words(l);
    return words(FNV_START_WORDS, w.length, w);
  }

  public static int get(Object noun) {
    return ( noun instanceof Cell )
      ? get((Cell) noun)
      : ( noun instanceof BigAtom )
      ? get((BigAtom) noun)
      : get((long) noun);
  }

  private static int fnv(int has) {
    return (has * ((int)16777619));
  }
  
  private static int out(int has) {
    return (has >>> 31) ^ (has & 0x7fffffff);
  }

  private static int in(int off, int nwd, int[] wod) {
    if (0 == nwd) {
      return off;
    }
    int i, x;
    for (i = 0; i < (nwd - 1); ++i) {
      x = wod[i];

      off = fnv(off ^ ((x >>> 0)  & 0xff));
      off = fnv(off ^ ((x >>> 8)  & 0xff));
      off = fnv(off ^ ((x >>> 16) & 0xff));
      off = fnv(off ^ ((x >>> 24) & 0xff));
    }
    x = wod[nwd - 1];
    if (x != 0) {
      off = fnv(off ^ (x & 0xff));
      x >>>= 8;
      if (x != 0) {
        off = fnv(off ^ (x & 0xff));
        x >>>= 8;
        if (x != 0) {
          off = fnv(off ^ (x & 0xff));
          x >>>= 8;
          if (x != 0) {
            off = fnv(off ^ (x & 0xff));
          }
        }
      }
    }
    return off;
  }
  
  public static int calculate(Cell c) {
    return both(get(c.head), get(c.tail));
  }

  public static int both(int a, int b) {
    int bot, out;
    while ( true ) {
      bot = fnv(a ^ fnv(b));
      out = out(bot);
      if ( 0 != out ) {
        return out;
      }
      else {
        ++b;
      }
    }
  }

  public static int words(int[] words) {
    return words(FNV_START_WORDS, words.length, words);
  }

  private static int words(int off, int nwd, int[] wod) {
    int has, out; 

    while ( true ) {
      has = in(off, nwd, wod);
      out = out(has);
      if ( 0 != out ) {
        return out;
      }
      ++off;
    }
  }

}
