package net.frodwith.jaque.dashboard;

public final class ColdRegistration {
  public final BatteryHash hash;
  public final Registration registration;

  public ColdRegistration(BatteryHash hash, Registration registration) {
    this.hash = hash;
    this.registration = registration;
  }
}
