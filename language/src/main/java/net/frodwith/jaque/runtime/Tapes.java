package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

public final class Tapes {
  public static Object runt(Object count, Object atom, Object list) {
    Atom.CountDown down = new Atom.CountDown(count);

    while ( !down.isZero() ) {
      list = new Cell(atom, list);
      down.next();
    }

    return list;
  }

  public static Cell trim(Object count, Object tape) throws ExitException {
    Lists.Iterator i = new Lists.Iterator(tape);
    Atom.CountDown down = new Atom.CountDown(count);
    ArrayDeque<Object> stack = new ArrayDeque<>();

    while ( !down.isZero() ) {
      if ( !i.hasNext() ) {
        break;
      }
      else {
        stack.push(i.next());
        down.next();
      }
    }

    Object a = 0L;
    while ( !stack.isEmpty() ) {
      a = new Cell(stack.pop(), a);
    }
    return new Cell(a, i.rest());
  }

  public static String toString(Object tape) throws ExitException {
    StringBuilder b = new StringBuilder();
    Lists.Iterator i = new Lists.Iterator(tape);
    while ( i.hasNext() ) {
      b.append((char) Atom.requireInt(i.next()));
    }
    return b.toString();
  }

  public static Object fromString(String t) {
    char[] cs = t.toCharArray();
    Object r = 0L;
    for (int i = cs.length - 1; i >= 0; --i ) {
      r = new Cell((long) cs[i], r);
    }
    return r;
  }
}
