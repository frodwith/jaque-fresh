package net.frodwith.jaque.data;

import java.util.Iterator;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;

import net.frodwith.jaque.exception.ExitException;

public class List implements Iterable<Object> {
  private Object noun;

  public List(Object noun) {
    this.noun = noun;
  }

  @Override
  public Iterator<Object> iterator() {
    return new Cursor(noun);
  }

  public class Cursor implements Iterator<Object> {
    private Object cur;

    public Cursor(Object noun) {
      this.cur = noun;
    }

    @Override
    public boolean hasNext() {
      return cur instanceof Cell;
    }

    @Override
    public Object next() {
      Cell c = (Cell) cur;
      cur = c.tail;
      return c.head;
    }
  }

  public static Object lent(Object ram) {
    Iterator<Object> i = new List(ram).iterator();
    Atom.CountUp c = new Atom.CountUp();
    Object r = 0L;

    while ( i.hasNext() ) {
      c.next();
      i.next();
    }

    return c.atom();
  }

  public static Object flop(Object a) {
    Object r = 0L;
    for ( Object i : new List(a) ) {
      r = new Cell(i, r);
    }
    return r;
  }

  // longest common subsequence
  // credit to: https://rosettacode.org/wiki/Longest_common_subsequence#Dynamic_Programming_3
  public static Object loss(Object a, Object b) throws ExitException {
    int i, j,
        lea = Atom.requireInt(lent(a)),
        leb = Atom.requireInt(lent(b));
    int[][] lens = new int[lea+1][leb+1];
    Object[] aa = new Object[lea],
             bb = new Object[leb];

    i = 0;
    for ( Object o : new List(a) ) {
      aa[i++] = o;
    }

    i = 0;
    for ( Object o : new List(b) ) {
      bb[i++] = o;
    }

    for ( i = 0; i < lea; ++i ) {
      for ( j = 0; j < leb; ++j ) {
        if ( Equality.equals(aa[i], bb[j]) ) {
          lens[i+1][j+1] = lens[i][j] + 1;
        }
        else {
          lens[i+1][j+1] = Math.max(lens[i+1][j], lens[i][j+1]);
        }
      }
    }

    Object r = 0L;
    for ( i = lea, j = leb;
          i != 0 && j != 0; ) {
      if ( lens[i][j] == lens[i-1][j] ) {
        --i;
      }
      else if ( lens[i][j] == lens[i][j-1] ) {
        --j;
      }
      else {
        assert aa[i-1] == bb[j-1];
        --i; --j;
        r = new Cell(aa[i],r);
      }
    }
    return r;
  }
}
