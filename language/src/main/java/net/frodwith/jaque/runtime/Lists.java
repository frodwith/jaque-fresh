package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;
import java.util.Deque;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.NounFunction;
import net.frodwith.jaque.NounPredicate;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.ExitException;

public final class Lists {
  // We can't use Iterator<Object> because we operate over any noun, and need to
  // bail (which should be checked) if we get an ill-formed noun.
  public static final class Iterator {
    private Object noun;

    public Iterator(Object noun) {
      this.noun = noun;
    }

    public boolean hasNext() throws ExitException {
      if ( noun instanceof Cell ) {
        return true;
      }
      else if ( Atom.isZero(noun) ) {
        return false;
      }
      else {
        CompilerDirectives.transferToInterpreter();
        throw new ExitException("invalid list");
      }
    }

    public Object next() throws ExitException {
      Cell c = Cell.require(noun);
      noun = c.tail;
      return c.head;
    }
  }

  public static Deque<Object> toDeque(Object list) throws ExitException {
    Deque<Object> s = new ArrayDeque<>();
    Iterator i = new Iterator(list);
    while ( i.hasNext() ) {
      s.push(i.next());
    }
    return s;
  }

  public static Object reel(NounFunction f, Object seed, Object list) 
    throws ExitException {
    Deque<Object> s = toDeque(list);
    while ( !s.isEmpty() ) {
      seed = f.apply(new Cell(s.pop(), seed));
    }
    return seed;
  }
  
  public static Object roll(NounFunction f, Object seed, Object list) 
    throws ExitException {
    Iterator i = new Iterator(list);
    while ( i.hasNext() ) {
      seed = f.apply(new Cell(i.next(), seed));
    }
    return seed;
  }
  
  public static Object turn(NounFunction f, Object list) throws ExitException {
    Deque<Object> s = toDeque(list);
    Object r = 0L;
    while ( !s.isEmpty() ) {
      r = new Cell(f.apply(s.pop()), r);
    }
    return r;
  }

  public static Object weld(Object a, Object b) throws ExitException {
    Deque<Object> s = toDeque(a);
    Object r = b;
    while ( !s.isEmpty() ) {
      r = new Cell(s.pop(), r);
    }
    return r;
  }
  
  public static boolean lien(NounPredicate f, Object list) throws ExitException {
    Iterator i = new Iterator(list);
    while ( i.hasNext() ) {
      if ( f.test(i.next()) ) {
        return true;
      }
    }
    return false;
  }

  public static boolean levy(NounPredicate f, Object list) throws ExitException {
    Iterator i = new Iterator(list); 
    while ( i.hasNext() ) {
      if ( !f.test(i.next()) ) {
        return false;
      }
    }
    return true;
  }

  public static Object lent(Object list) throws ExitException {
    Iterator i = new Iterator(list);
    Atom.CountUp c = new Atom.CountUp();

    while ( i.hasNext() ) {
      c.next();
    }

    return c.atom();
  }

  public static Object slag(Object a, Object b) throws ExitException {
    while ( !Atom.isZero(a) ) {
      if ( !(b instanceof Cell) ) {
        return 0L;
      }
      b = Cell.require(b).tail;
      a = HoonMath.dec(a);
    }
    return b;
  }

  public static Object flop(Object a) throws ExitException {
    Iterator i = new Iterator(a);
    Object r = 0L;
    while ( i.hasNext() ) {
      r = new Cell(i.next(), r);
    }
    return r;
  }
  
  // longest common subsequence
  // credit to: https://rosettacode.org/wiki/Longest_common_subsequence#Dynamic_Programming_3
  public static Object loss(Object a, Object b) throws ExitException {
    Iterator li;
    int i, j, lea = Atom.requireInt(lent(a)), leb = Atom.requireInt(lent(b));
    int[][] lens = new int[lea+1][leb+1];
    Object[] aa = new Object[lea], bb = new Object[leb];
    
    li = new Iterator(a);
    i  = 0;
    while ( li.hasNext() ) {
      aa[i++] = li.next();
    }
    
    li = new Iterator(b);
    i  = 0;
    while ( li.hasNext() ) {
      bb[i++] = li.next();
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
