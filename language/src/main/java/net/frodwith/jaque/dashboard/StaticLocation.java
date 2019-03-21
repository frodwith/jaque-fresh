package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.LocatedClass;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.runtime.NockContext;

public abstract class StaticLocation extends Location {
  protected StaticLocation(String name, Map<String,Hook> hooks) {
    super(name, hooks);
  }

  @Override
  public final LocatedFine buildFine(Cell core, Dashboard dashboard) throws ExitException {
    return new StaticFine(core, (LocatedClass) core.getMeta().getClass(core, dashboard));
  }

  @Override
  public final boolean copyableEdit(Axis axis) {
    return false;
  }
}
