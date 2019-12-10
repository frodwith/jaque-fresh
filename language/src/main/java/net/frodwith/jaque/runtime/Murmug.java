package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;

import de.greenrobot.common.hash.Murmur3A;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;


public final class Murmug {
  private enum Type { ROOT, HEAD, TAIL };

  @TruffleBoundary
  public static int get(Object veb) {
    final class MugFrame {
      public Type type;
      public Cell cell;
      public int headMug;

      public MugFrame(Type type, Cell cell, int headMug) {
        this.type = type;
        this.cell = cell;
        this.headMug = headMug;
      }
    }

    ArrayDeque<MugFrame> stack = new ArrayDeque<>();
    stack.push(new MugFrame(Type.ROOT, null, 0));
    int currentMug = 0;

    advance:
    while (true) {
      // veb is a direct atom, mug is not memoized.
      //
      if (veb instanceof Long) {
        currentMug = bytes(Atom.toByteArray((long)veb));
        // goto retreat
      }
      else if (veb instanceof BigAtom) {
        BigAtom ba = (BigAtom)veb;
        int baMug = ba.cachedMug();
        if (baMug != 0) {
          currentMug = baMug;
        } else {
          currentMug = bytes(ba.asByteArray());
          ba.setMug(currentMug);
        }
        // goto retreat
      }
      else if (veb instanceof Cell) {
        Cell c = (Cell)veb;
        int cMug = c.cachedMug();
        if (cMug != 0) {
          currentMug = cMug;
          // goto retreat
        } else {
          stack.push(new MugFrame(Type.HEAD, c, 0));
          veb = c.head;
          continue advance;
        }
      }

      retreat:
      while (true) {
        MugFrame frame = stack.pop();

        switch (frame.type) {
          case ROOT: {
            // We're done
            break advance;
          }

          case HEAD: {
            stack.push(new MugFrame(Type.TAIL, frame.cell, currentMug));
            veb = frame.cell.tail;
            continue advance;
          }

          case TAIL: {
            Cell cel = frame.cell;
            currentMug = both(frame.headMug, currentMug);
            cel.setMug(currentMug);
            continue retreat;
          }
        }
      }
    }

    assert stack.isEmpty();

    return currentMug;
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
