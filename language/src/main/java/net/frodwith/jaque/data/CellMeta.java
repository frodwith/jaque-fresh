package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.exception.ExitException;

public final class CellMeta {
  private int mug;
  public  Cell cell;

  private NockFunction function;
  private NockObject object;
  private Battery battery;

  public CellMeta(Cell cell, int mug) {
    this.cell      = cell;
    this.mug       = mug;
    this.battery   = null;
    this.object    = null;
    this.function  = null;
  }

  public NockObject getObject(NockContext context) throws ExitException {
    if ( !hasObject() ) {
      object = context.dashboard.getObject(cell);
    }
    return object;
  }

  public void setObject(NockObject object) {
    this.object = object;
  }

  public boolean hasObject() {
    if ( null == object ) {
      return false;
    }
    else if ( object.klass.valid.isValid() ) {
      return true;
    }
    else {
      this.object = null;
      return false;
    }
  }

  public NockObject cachedObject() {
    return hasObject() ? object : null;
  }

  public void writeObject(Cell edited, Axis written) {
    if ( hasObject() && object.klass.copyableEdit(written) ) {
      edited.getMeta().object = object.like(edited);
    }
  }

  public boolean knownAt(Location location) {
    return hasObject() && object.klass.locatedAt(location);
  }

  public Battery getBattery(NockContext context) {
    if ( null == battery ) {
      battery = context.dashboard.getBattery(cell);
    }
    return battery;
  }

  public NockFunction getFunction(NockContext context) throws ExitException {
    if ( null == function ) {
      function = context.lookupFunction(cell);
    }
    return function;
  }

  public int mug() {
    if ( 0 == mug ) {
      mug = Mug.calculate(cell);
    }
    return mug;
  }

  public void setMug(int mug) {
    this.mug = mug;
  }

  public int cachedMug() {
    return mug;
  }

  public void unify(CellMeta other) {
    other.cell = cell;
    boolean mine = false, his = false;

    // mugs
    if ( 0 == mug ) {
      mug = other.mug;
    }
    else if ( 0 == other.mug ) {
      other.mug = mug;
    }

    // batteries
    if ( null == battery ) {
      battery = other.battery;
      his = true;
    }
    else if ( null == other.battery ) {
      other.battery = battery;
      mine = true;
    }

    // functions
    if ( null == function ) {
      function = other.function;
      his = true;
    }
    else if ( null == other.function ) {
      other.function = function;
      mine = true;
    }

    // objects
    if ( null == object ) {
      object = other.object;
      his = true;
    }
    else {
      other.object = object;
      mine = true;
    }
  }
}
