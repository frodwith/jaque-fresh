package net.frodwith.jaque.runtime;

import de.greenrobot.common.hash.Murmur3A;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class Murmug {
  @TruffleBoundary
  public static int get(Cell c) {
    return c.mug();
  }

  @TruffleBoundary
  public static int get(BigAtom a) {
    return a.getMug();
  }

  @TruffleBoundary
  public static int get(long l) {
    return bytes(Atom.toByteArray(l));
  }

  @TruffleBoundary
  public static int get(Object noun) {
    return ( noun instanceof Cell )
      ? get((Cell) noun)
      : ( noun instanceof BigAtom )
      ? get((BigAtom) noun)
      : get((long) noun);
  }

  public static int calculate(Cell c) {
    return both(get(c.head), get(c.tail));
  }

  public static int calculate(BigAtom a) {
    return bytes(a.asByteArray());
  }

  public static int both(int a, int b) {
    return get((long)(a ^ (0x7fffffff ^ b)));
  }

  public static int bytes(byte[] b) {
    Murmur3A murmur;
    int  seed = 0xcafebabe;
    long hash = 0L;

    while ( true ) {
      murmur = new Murmur3A(seed);
      murmur.update(b);
      hash = murmur.getValue();
      hash = (hash >>> 31) ^ (hash & 0x7fffffffL);

      if ( 0L == hash ) {
        ++seed;
      }
      else {
        return (int)hash;
      }
    }
  }
}
