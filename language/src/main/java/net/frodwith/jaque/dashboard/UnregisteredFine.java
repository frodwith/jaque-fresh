package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;

public final class UnregisteredFine extends UnlocatedFine {
  public UnregisteredFine(Cell battery) {
    super(battery);
  }

  @Override
  public boolean extraChecks(Cell core, NockContext context) {
    return true;
  }
}
