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
  public final FineCheck getFine(Cell core, NockContext context)
    throws ExitException {
    return new UnregisteredFine(Cell.require(core.head));
  }

  @Override
  public final boolean copyableEdit(Axis written) {
    return written.inTail();
  }
}
