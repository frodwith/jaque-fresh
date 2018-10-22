package net.frodwith.jaque.data;

import java.util.Map;

public final class Location {
  public final AxisMap<NockFunction> drivers;
  public final Axis toParent;

  public Location(AxisMap<NockFunction> drivers, Axis toParent) {
    this.drivers = drivers;
    this.toParent = toParent;
  }
}
