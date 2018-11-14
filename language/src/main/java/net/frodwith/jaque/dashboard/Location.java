package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;

public abstract class Location {
  public final String name;
  public final Map<String,Hook> hooks;

  protected Location(String name, Map<String,Hook> hooks) {
    this.name = name;
    this.hooks = hooks;
  }

  public abstract FineCheck
    buildFine(Cell core, Supplier<Dashboard> supply)
      throws ExitException;

  public abstract boolean copyableEdit(Axis axis);
}
