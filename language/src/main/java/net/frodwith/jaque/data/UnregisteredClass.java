package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.UnregisteredFine;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

public final class UnregisteredClass extends UnlocatedClass {
  public UnregisteredClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public FineCheck buildFine(Cell core) throws ExitException {
    return new UnregisteredFine(Cell.require(core.head));
  }
}
