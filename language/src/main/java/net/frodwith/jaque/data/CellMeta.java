package net.frodwith.jaque.data;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import net.frodwith.jaque.runtime.Dashboard;
import net.frodwith.jaque.runtime.Mug;

public final class CellMeta {
  private int mug;
  private final Cell cell;
  private final Dashboard dashboard;

  private NockBattery battery;
  private NockObject object;

  public CellMeta(Dashboard dashboard, Cell cell, int mug) {
    this.dashboard = dashboard;
    this.cell      = cell;
    this.mug       = mug;
    this.battery   = null;
    this.object    = null;
  }

  public NockObject getObject() {
    if ( null == object || !object.valid.isValid() ) {
      object = dashboard.getObject(cell);
    }
    return object;
  }

  public NockBattery getBattery() {
    if ( null == battery ) {
      battery = dashboard.getBattery(cell);
    }
    return battery;
  }

  public int mug() {
    if ( 0 == mug ) {
      mug = Mug.calculate(cell);
    }
    return mug;
  }

  public int cachedMug() {
    return mug;
  }
}
