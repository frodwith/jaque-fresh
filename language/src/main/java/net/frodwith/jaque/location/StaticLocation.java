package net.frodwith.jaque.location;

import java.util.Map;

import net.frodwith.jaque.data.Cell;

public abstract class StaticLocation extends Location {
  protected StaticLocation(String name, Map<String,Hook> hooks) {
    super(name, hooks);
  }

  public final FineCheck buildFine(FineBuilder fine, Cell core) {
    return fine.finalize(core);
  }
}
