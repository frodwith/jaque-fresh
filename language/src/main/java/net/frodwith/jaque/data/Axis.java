package net.frodwith.jaque.data;

import java.util.Iterator;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Equality;

public final class Axis implements Iterable<Axis.Fragment> {
  public enum Fragment { HEAD, TAIL }

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
  
  public Axis(Object atom) {
    this.atom   = atom;
    this.length = HoonMath.met(atom) - 1;
  }

  @Override
  public Iterator<Fragment> iterator() {
    return new Cursor();
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

  public boolean inside(Axis parent) {
    return subAxis(atom, parent.atom);
  }
}
