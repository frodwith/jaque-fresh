package net.frodwith.jaque.location;

import java.util.Map;

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
  protected FineCheck
    buildFine(FineBuilder fine, Cell core)
      throws ExitException {
    fine.addBattery(Cell.require(core.head));
    return parent.buildFine(fine, toParent.fragment(core));
  }
}
