package net.frodwith.jaque.dashboard;

import java.util.Map;

import net.frodwith.jaque.data.Axis;

public final class RootLocation extends StaticLocation {
  public final Object payload;

  public RootLocation(String name, Map<String,Hook> hooks, Object payload) {
    super(name, hooks);
    this.payload = payload;
  }

  @Override
  public void register(Registration registration) {
    registration.registerRoot(payload, this);
  }
}
