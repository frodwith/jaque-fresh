package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.RegisteredClass;
import net.frodwith.jaque.exception.ExitException;

public final class RegisteredFine extends UnlocatedFine {
  public RegisteredFine(Cell battery) {
    super(battery);
  }

  @Override
  public boolean extraChecks(Cell core, Supplier<Dashboard> supply) {
    try {
      return core.getMeta().getObject(supply).klass instanceof RegisteredClass;
    }
    catch ( ExitException e ) {
      return false;
    }
  }
}
