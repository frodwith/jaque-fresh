package net.frodwith.jaque.data;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.IOException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.printer.MappedNounPrinter;

import net.frodwith.jaque.exception.ExitException;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public final class Cell implements TruffleObject, Serializable {
  // head and tail are not final because we set them during unifying equals
  public Object head, tail;
  private transient Object info;
  
  public Cell(Object head, Object tail) {
    this.head = head;
    this.tail = tail;
    this.info = null;
  }

  public static Cell require(Object o) throws ExitException {
    if ( o instanceof Cell ) {
      return (Cell) o;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new ExitException("cell required");
    }
  }

  public void unifyInfo(Cell other) {
    Object oi = other.info;
    if ( null == info ) {
      if ( null != oi ) {
        info = oi;
      }
    }
    else if ( null == oi ) {
      if ( null != info ) {
        other.info = info;
      }
    }
    else if ( info instanceof Integer ) {
      if ( !(oi instanceof Integer) ) {
        ((CellInfo)oi).setMug((int) info);
        info = oi;
      }
    }
    else if ( oi instanceof Integer ) {
      if ( !(info instanceof Integer) ) {
        ((CellInfo)info).setMug((int) oi);
        other.info = info;
      }
    }
    else {
      CellInfo mine = (CellInfo) info;
      CellInfo theirs = (CellInfo) oi;
      mine.unify(theirs);
      other.info = mine;
    }
  }

  public void unifyHeads(Cell other) {
    if ( head instanceof Cell ) {
      ((Cell)head).unifyInfo((Cell)other.head);
    }
    else if ( head instanceof BigAtom ) {
      ((BigAtom)other.head).words = ((BigAtom)head).words;
    }
    other.head = head;
  }

  public void unifyTails(Cell other) {
    if ( tail instanceof Cell ) {
      ((Cell)tail).unifyInfo((Cell)other.tail);
    }
    else if ( tail instanceof BigAtom ) {
      ((BigAtom)other.tail).words = ((BigAtom)tail).words;
    }
    other.tail = tail;
  }

  public static boolean unequalMugs(Cell a, Cell b) {
    return a.hasMug() && b.hasMug() && a.getMug() != b.getMug();
  }

  private boolean hasInfo() {
    return info instanceof CellInfo;
  }

  // Only call this if you know hasInfo() is true.
  private CellInfo getInfo() {
    return (CellInfo) info;
  }

  private CellInfo forceInfo() {
    if ( hasInfo() ) {
      return getInfo();
    }
    else {
      CellInfo ci = new CellInfo((int) info);
      info = ci;
      return ci;
    }
  }

  public boolean knownAt(Dashboard dashboard, Location location) {
    return hasInfo() && getInfo().knownAt(dashboard, location);
  }

  public void copyObject(Cell from, Axis written) {
    if ( from.hasInfo() ) {
      from.getInfo().writeObject(this, written);
    } 
  }

  public boolean hasMug() {
    return hasInfo()
      ? getInfo().hasMug()
      : 0 != ((int) info);
  }

  public boolean hasFunction() {
    return hasInfo() && getInfo().hasFunction();
  }

  public boolean hasObject() {
    return hasInfo() && getInfo().hasObject();
  }

  public boolean hasBattery() {
    return hasInfo() && getInfo().hasBattery();
  }

  public int getMug() {
    if ( hasInfo() ) {
      return getInfo().getMug();
    }
    else {
      int i = (int) info;
      if ( 0 == i ) {
        i = Mug.calculate(this);
        info = i;
      }
      return i;
    }
  }

  public NockFunction getFunction(FormulaParser parser) {
    return forceInfo().getFunction(parser, this);
  }

  public NockObject getObject(Dashboard dashboard) {
    return forceInfo().getObject(dashboard, this);
  }

  public Battery getBattery(Dashboard dashboard) {
    return forceInfo().getBattery(dashboard, this);
  }

  @Override
  public int hashCode() {
    return getMug();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Cell) && Equality.equals(this, (Cell) o);
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return CellMessageResolutionForeign.ACCESS;
  }

  /* for debugging */
  public String pretty() {
    StringWriter w = new StringWriter();
    try {
      MappedNounPrinter.print(w, this);
    }
    catch ( ExitException | IOException e ) {
    }
    return w.toString();
  }
}
