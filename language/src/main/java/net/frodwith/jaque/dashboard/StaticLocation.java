package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

public abstract class StaticLocation extends Location {
  protected StaticLocation(String name, Map<String,Hook> hooks) {
    super(name, hooks);
  }

  public final LocatedFine
    buildFine(Cell core, Supplier<Dashboard> supply)
      throws ExitException {
    return new StaticFine(core, core.getMeta().getObject(supply));
  }

  public final boolean copyableEdit(Axis axis) {
    return false;
  }
}
