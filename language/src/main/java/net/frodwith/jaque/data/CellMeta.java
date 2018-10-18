package net.frodwith.jaque.data;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import net.frodwith.jaque.exception.Fail;

import net.frodwith.jaque.runtime.Dashboard;
import net.frodwith.jaque.runtime.Mug;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockFunctionRegistry;

public final class CellMeta {
  private int mug;
  private Cell cell;

  private NockFunction function;
  private NockBattery battery;
  private NockObject object;

  public CellMeta(Cell cell, int mug) {
    this.cell      = cell;
    this.mug       = mug;
    this.battery   = null;
    this.object    = null;
    this.function  = null;
  }

  public NockObject getObject(Dashboard dashboard) {
    if ( null == object || !object.valid.isValid() ) {
      object = dashboard.getObject(cell);
    }
    return object;
  }

  public NockBattery getBattery(Dashboard dashboard) {
    if ( null == battery ) {
      battery = dashboard.getBattery(cell);
    }
    return battery;
  }

  public NockFunction getFunction(NockFunctionRegistry registry) throws Fail {
    if ( null == function ) {
      function = registry.lookup(cell);
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