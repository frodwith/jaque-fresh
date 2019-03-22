package net.frodwith.jaque.dashboard;

import java.util.Optional;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

public final class RegisteredClass extends UnlocatedClass {
  public RegisteredClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final FineCheck buildFine(Cell core) throws ExitException {
    return new RegisteredFine(Cell.require(core.head), this);
  }

  public Optional<Location> locate(Cell core, Cell batteryCell) {
    return battery.locate(core, batteryCell);
  }
}
