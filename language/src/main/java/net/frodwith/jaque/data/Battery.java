package net.frodwith.jaque.data;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.location.Registration;

public final class Battery {
  public final byte[] hash;
  public final Cell noun;
  public final Registration registration;
  public final Assumption valid;

  public Battery(byte[] hash, Cell noun, Registration registration, Assumption valid) {
    this.hash = hash;
    this.noun = noun;
    this.registration = registration;
    this.valid = valid;
  }
}
