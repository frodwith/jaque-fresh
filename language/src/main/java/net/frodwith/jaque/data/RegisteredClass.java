package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.RegisteredFine;

public final class RegisteredClass extends UnlocatedClass {
  public final Registration registration;
  
  public RegisteredClass(Battery battery, Assumption stable,
                         Registration registration) {
    super(battery, stable);
    this.registration = registration;
  }

  @Override
  public final FineCheck getFine(Cell core, Supplier<Dashboard> supply) {
    return new RegisteredFine(core);
  }

  @Override
  public final boolean copyableEdit(Axis written) {
    return registration.copyableEdit(written);
  }
}
