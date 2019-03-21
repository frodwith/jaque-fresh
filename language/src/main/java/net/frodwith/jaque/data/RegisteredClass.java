package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.RegisteredFine;
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
