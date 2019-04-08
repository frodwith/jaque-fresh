package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class RegisteredFine extends UnlocatedFine {
  private final RegisteredClass klass;

  public RegisteredFine(Cell battery, RegisteredClass klass) {
    super(battery);
    this.klass = klass;
  }

  @Override
  public boolean extraChecks(Cell core, Dashboard dashboard) {
    CellMeta meta = core.getMeta();
    Optional<NockClass> cachedClass = meta.cachedClass(dashboard);
    if ( cachedClass.isPresent() ) {
      return cachedClass.get() instanceof UnlocatedClass;
    }
    else {
      Optional<Location> location = klass.locate(core, battery);
      if ( location.isPresent() ) {
        meta.setNockClass(dashboard.locatedClass(battery, location.get()));
        return false;
      }
      else {
        meta.setNockClass(klass);
        return true;
      }
    }
  }
}
