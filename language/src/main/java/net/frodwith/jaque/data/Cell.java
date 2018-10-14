package net.frodwith.jaque.data;

import java.io.Serializable;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.CellRequiredException;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public final class Cell implements TruffleObject, Serializable {
  // head and tail are not final because we set them during unifying equals
  public Object head, tail;
  private Object meta;
  
  public Cell(Object head, Object tail) {
    this.head = head;
    this.tail = tail;
    this.meta = null;
  }

  public static Cell require(Object o) throws CellRequiredException {
    if ( o instanceof Cell ) {
      return (Cell) o;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new CellRequiredException(o);
    }
  }

  @Override
  public int hashCode() {
    int mug;
    if ( null == meta ) {
      meta = mug = Mug.calculate(this);
    }
    else if ( meta instanceof Integer ) {
      mug = (int) meta;
    }
    else {
      mug = ((CellMeta) meta).mug();
    }
    return mug;
  }

  public int cachedMug() {
    if ( null == meta ) {
      return 0;
    }
    else if ( meta instanceof Integer ) {
      return (int) meta;
    }
    else {
      return ((CellMeta)meta).cachedMug();
    }
  }

  public static boolean unequalMugs(Cell a, Cell b) {
    int am, bm;
    if ( 0 == (am = a.cachedMug()) ) {
      return false;
    }
    if ( 0 == (bm = b.cachedMug()) ) {
      return false;
    }
    return am == bm;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Cell) && Equality.equals(this, (Cell) o);
  }

  public ForeignAccess getForeignAccess() {
    return CellMessageResolutionForeign.ACCESS;
  }
}
