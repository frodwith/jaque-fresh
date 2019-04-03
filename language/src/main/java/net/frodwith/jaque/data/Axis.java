package net.frodwith.jaque.data;

import java.util.Iterator;

import com.oracle.truffle.api.nodes.ExplodeLoop;
import net.frodwith.jaque.exception.ExitException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Equality;

public final class Axis implements Iterable<Axis.Fragment> {
  public enum Fragment { HEAD, TAIL }

  public final static Axis CRASH    = new Axis(0L);
  public final static Axis IDENTITY = new Axis(1L);
  public final static Axis HEAD     = new Axis(2L);
  public final static Axis TAIL     = new Axis(3L);
  public final static Axis SAMPLE   = new Axis(6L);
  public final static Axis CONTEXT  = new Axis(7L);

  public final int length;
  public final Object atom;

  public final class Cursor implements Iterator<Fragment> {
    private int n;
    
    public Cursor() {
      this.n = length - 1;
    }

    @Override
    public boolean hasNext() {
      return n >= 0;
    }

    @Override
    public Fragment next() {
      return Atom.getNthBit(atom, n--) ? Fragment.TAIL : Fragment.HEAD;
    }
  }
  
  private Axis(Object atom) {
    this.atom   = atom;
    this.length = HoonMath.met(atom) - 1;
  }

  public static Axis require(Object noun) throws ExitException {
    Object atom = Atom.require(noun);
    return get(atom);
  }

  public static Axis get(Object atom) {
    if ( atom instanceof Long ) {
      switch ( ((Long) atom).intValue() ) {
        case 0:
          return Axis.CRASH;
        case 1:
          return Axis.IDENTITY;
        case 2:
          return Axis.HEAD;
        case 3:
          return Axis.TAIL;
        case 6:
          return Axis.SAMPLE;
        case 7:
          return Axis.CONTEXT;
      }
    }
    return new Axis(atom);
  }

  public Axis mas() {
    return get(HoonMath.mas(atom));
  }

  public Axis peg(int under) {
    return peg((long) (under & 0xFFFFFFFF));
  }

  public Axis peg(long under) {
    return new Axis(HoonMath.peg(this.atom, under));
  }

  public Axis peg(Axis under) {
    return new Axis(HoonMath.peg(this.atom, under.atom));
  }

  public boolean isIdentity() {
    return this == IDENTITY;
  }

  public boolean isCrash() {
    return this == CRASH;
  }

  public boolean inHead() {
    return subAxis(atom, 2L);
  }

  public boolean inTail() {
    return subAxis(atom, 3L);
  }

  @Override
  public Iterator<Fragment> iterator() {
    return new Cursor();
  }

  @TruffleBoundary
  public Object edit(Object noun, Object value) throws ExitException {
    // XX: could explode this with a stack
    if ( 0 == length ) {
      return value;
    }
    else {
      Cell c = Cell.require(noun);
      return inHead()
        ? new Cell(mas().edit(c.head, value), c.tail)
        : new Cell(c.head, mas().edit(c.tail, value));
    }
  }

  @ExplodeLoop
  public Object fragment(Object noun) throws ExitException {
    for ( int i = length - 1; i >= 0; --i ) {
      Cell c = Cell.require(noun);
      noun = ( Atom.getNthBit(atom, i) )
           ? c.tail
           : c.head;
    }
    return noun;
  }

  // XX: is there a hoon equivalent of subAxis?
  public static boolean subAxis(long child, long parent) {
    switch ( Long.compareUnsigned(child, parent) ) {
      case 0:
        return true;
      case -1:
        return false;
      case 1:
        int cz = Long.numberOfLeadingZeros(child),
            pz = Long.numberOfLeadingZeros(parent),
            shift = pz - cz;
        return (child >>> shift) == parent;
      default:
        throw new AssertionError();
    }
  }

  public static boolean subAxis(Object child, Object parent) {
    if ( child instanceof Long ) {
      if ( parent instanceof Long ) {
        return subAxis((long) child, (long) parent);
      }
      else {
        return false;
      }
    }
    else {
      int childLen = HoonMath.met(child),
          parentLen = HoonMath.met(parent);
      if ( childLen < parentLen ) {
        return false;
      }
      else {
        Object chopped = HoonMath.rsh((byte)0, childLen - parentLen, child);
        return Equality.equals(chopped, parent);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Axis) && Equality.equals(atom, ((Axis)o).atom);
  }

  @Override
  public int hashCode() {
    return Mug.get(atom);
  }

  public boolean inside(Axis parent) {
    return subAxis(atom, parent.atom);
  }

  public static Axis parseOption(Object option) {
    try {
      return Axis.require(Atom.parseOption(option));
    }
    catch ( ExitException e ) {
      throw new IllegalArgumentException(option.toString());
    }
  }
}
