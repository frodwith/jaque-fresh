package net.frodwith.jaque.dashboard;

import net.frodwith.jaque.data.Cell;

public final class ColdRegistration {
  public final Battery battery;
  public final Registration registration;

  public ColdRegistration(Registration registration, Battery battery) {
    this.battery = battery;
    this.registration = registration;
  }
}
