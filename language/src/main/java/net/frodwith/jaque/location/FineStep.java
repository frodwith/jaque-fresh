package net.frodwith.jaque.location;

public final class FineStep {
  public final Cell battery;
  public final Axis toParent;

  public FineStep(Cell battery, Axis toParent) {
    this.battery = battery;
    this.toParent = toParent;
  }
}
