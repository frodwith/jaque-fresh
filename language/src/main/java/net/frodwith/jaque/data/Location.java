package net.frodwith.jaque.data;

import java.util.Map;

import net.frodwith.jaque.runtime.NockFunction;

public final class Location {
  public final AxisMap<NockFunction> drivers;
  public final Object axisToParent;

  public Location(AxisMap<NockFunction> drivers, Object axisToParent) {
    this.drivers = drivers;
    this.axisToParent = axisToParent;
  }
}
