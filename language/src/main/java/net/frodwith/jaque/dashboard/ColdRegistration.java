package net.frodwith.jaque.dashboard;

public final class ColdRegistration {
  public final BatteryHash hash;
  public final Registration registration;

  public ColdRegistration(BatteryHash hash) {
    this.hash = hash;
    this.registration = new Registration();
  }
}
