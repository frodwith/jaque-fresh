package net.frodwith.jaque.dashboard;

import java.util.Map;

import net.frodwith.jaque.data.Axis;

public final class StaticChildLocation extends StaticLocation {
  public final StaticLocation parent;

  public StaticChildLocation(String name,
                             Map<String,Hook> hooks,
                             StaticLocation parent) {
    super(name, hooks);
    this.parent = parent;
  }

  @Override
  public void register(Registration registration) {
    registration.registerChild(Axis.TAIL, this, parent);
  }
}
