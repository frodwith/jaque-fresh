package net.frodwith.jaque.dashboard;

import net.frodwith.jaque.data.Cell;

public final class ColdRegistration {
  private BatteryHash hash;
  private Cell battery;
  public final Registration registration;

  public ColdRegistration(Registration registration,
      Cell battery, BatteryHash hash) {
    this.hash = hash; // may be null
    this.battery = battery;
    this.registration = registration;
  }

  public BatteryHash getHash() {
    if ( null == hash ) {
      hash = BatteryHash.hash(battery);
    }
    return hash;
  }

  public BatteryHash cachedHash() {
    return hash;
  }
}
