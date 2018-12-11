package net.frodwith.jaque.dashboard;

public final class ColdRegistration {
  public final BatteryHash hash;
  public final Registration registration;

  public ColdRegistration(Registration registration, BatteryHash hash) {
    this.hash = hash;
    this.registration = registration;
  }
}
