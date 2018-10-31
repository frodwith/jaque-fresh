package net.frodwith.jaque.location;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Axis;

public final class FineStep {
  public final Cell battery;
  public final Axis toParent;

  public FineStep(Cell battery, Axis toParent) {
    this.battery = battery;
    this.toParent = toParent;
  }
}
