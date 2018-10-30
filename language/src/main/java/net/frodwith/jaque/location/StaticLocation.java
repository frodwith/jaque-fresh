package net.frodwith.jaque.location;

import java.util.Map;

public abstract class StaticLocation extends Location {
  protected StaticLocation(String name, Map<String,Hook> hooks) {
    super(name, hooks);
  }

  public final FineCheck buildFine(FineBuilder fine, Cell core) {
    return fine.finalize(core);
  }
}
