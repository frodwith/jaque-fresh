package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;

public final class UnregisteredFine extends UnlocatedFine {
  public UnregisteredFine(Cell battery) {
    super(battery);
  }

  @Override
  public boolean extraChecks(Cell core, Supplier<Dashboard> supply) {
    return true;
  }
}
