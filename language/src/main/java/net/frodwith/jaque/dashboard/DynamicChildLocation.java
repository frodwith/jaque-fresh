package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;

import net.frodwith.jaque.exception.ExitException;

public final class DynamicChildLocation extends Location {
  public final Location parent;
  public final Axis toParent;

  public DynamicChildLocation(String name,
                              Map<String,Hook> hooks,
                              Location parent,
                              Axis toParent) {
    super(name, hooks);
    this.parent = parent;
    this.toParent = toParent;
  }

  @Override
  protected LocatedFine buildFine(Cell core, Supplier<Dashboard> supply) {
    LocatedFine located;
    FineCheck parentFine = Cell.require(toParent.fragment(core))
      .getMeta().getObject(supply).getFine();
    try {
      located = (LocatedFine) parentFine;
    }
    catch ( ClassCastException e ) {
      // If you only pass me cores you know are located here, this will
      // never happen.
      CompilerDirectives.transferToInterpeter();
      throw new AssertionError();
    }
    return located.addStep(step);
  }
}
