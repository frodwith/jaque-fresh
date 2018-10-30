package net.frodwith.jaque.data;

public final class Battery {
  public final byte[] hash;
  public final Registration registration;
  public final Assumption valid;

  public Battery(byte[] hash, Registration registration, Assumption valid) {
    this.hash = hash;
    this.registration = registration;
    this.valid = valid;
  }
}
