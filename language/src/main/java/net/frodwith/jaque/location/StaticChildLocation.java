package net.frodwith.jaque.location;

import java.util.Map;

public final class StaticChildLocation extends StaticLocation {
  public final StaticLocation parent;

  public StaticChildLocation(String name,
                             Map<String,Hook> hooks,
                             StaticLocation parent) {
    super(name, hooks);
    this.parent = parent;
  }
}
