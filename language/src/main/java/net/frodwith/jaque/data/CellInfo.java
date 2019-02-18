package net.frodwith.jaque.data;

import net.frodwith.jaque.dashboard.Dashboard;

public final class CellInfo {
  private int mug;
  private NockFunction cachedFunction;
  private NockObject cachedObject;
  private Battery cachedBattery;

  public CellInfo(int mug) {
    this.mug = mug;
    this.cachedFunction = null;
    this.cachedObject = null;
    this.cachedBattery = null;
  }

  public boolean hasMug() {
    return 0 != mug;
  }

  public boolean hasFunction() {
    return null != cachedFunction;
  }

  public boolean hasObject(Dashboard dashboard) {
    return null != cachedObject && cachedObject.isValid(dashboard);
  }

  public boolean hasBattery(Dashboard dashboard) {
    return null != cachedBattery && cachedBattery.isValid(dashboard);
  }

  public int getMug(Cell c) {
    if ( !hasMug() ) {
      mug = Mug.calculate(c);
    }
    return mug;
  }

  public NockFunction getFunction(FormulaParser parser, Cell formula) {
    if ( !hasFunction() ) {
      cachedFunction = parser.parse(formula);
    }
    return cachedFunction;
  }

  public NockObject getObject(Dashboard dashboard, Cell core) {
    if ( !hasObject(dashboard) ) {
      cachedObject = dashboard.createObject(core);
    }
    return cachedObject;
  }

  public Battery getBattery(Dashboard dashboard, Cell battery) {
    if ( !hasBattery(dashboard) ) {
      cachedBattery = dashboard.createBattery(battery);
    }
    return cachedBattery;
  }

  public void unify(CellInfo other) {
    // If one object has a field and the other does not, fill the missing one.
    // If both or neither have the field, leave as is.

    // mugs
    if ( 0 == mug ) {
      mug = other.mug;
    }
    else if ( 0 == other.mug ) {
      other.mug = mug;
    }

    // batteries
    if ( null == cachedBattery ) {
      cachedBattery = other.cachedBattery;
    }
    else if ( null == other.cachedBattery ) {
      other.cachedBattery = cachedBattery;
    }

    // functions
    if ( null == cachedFunction ) {
      cachedFunction = other.cachedFunction;
    }
    else if ( null == other.cachedFunction ) {
      other.cachedFunction = cachedFunction;
    }

    // objects
    if ( null == cachedObject ) {
      cachedObject = other.cachedObject;
    }
    else if ( null == other.cachedObject ) {
      other.cachedObject = cachedObject;
    }
  }
}
