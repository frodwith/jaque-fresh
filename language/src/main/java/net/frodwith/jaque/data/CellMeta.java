package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.runtime.Dashboard;
import net.frodwith.jaque.runtime.Mug;

import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.runtime.NockContext;

public final class CellMeta {
  private int mug;
  private Cell cell;

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

  public NockObject getObject(Supplier<Dashboard> supply) {
    if ( !hasObject() ) {
      object = supply.get().getObject(cell);
    }
    return object;
  }

  public boolean hasObject() {
    if ( null == object ) {
      return false;
    }
    else if ( object.valid.isValid() ) {
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
    if ( hasObject() && object.copyableEdit(edited, written) ) {
      edited.getMeta().object = object.like(edited);
    }
  }

  public Battery getBattery(Supplier<Dashboard> supply) {
    if ( null == battery ) {
      battery = supply.get().getBattery(cell);
    }
    return battery;
  }

  public NockFunction getFunction(Supplier<NockFunctionRegistry> supply) 
    throws ExitException {
    if ( null == function ) {
      function = supply.get().lookup(cell);
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
    }
    else if ( null == other.battery ) {
      other.battery = battery;
    }

    // functions
    if ( null == function ) {
      function = other.function;
    }
    else if ( null == other.function ) {
      other.function = function;
    }

    // objects
    if ( null == object ) {
      object = other.object;
    }
    else {
      other.object = object;
    }
  }
}
