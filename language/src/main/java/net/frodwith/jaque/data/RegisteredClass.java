package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.RegisteredFine;

public final class RegisteredClass extends UnlocatedClass {
  public RegisteredClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final FineCheck getFine(Cell core, NockContext context) {
    return new RegisteredFine(core);
  }
}
