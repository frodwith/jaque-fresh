package net.frodwith.jaque.jet;

import java.util.Map;

import com.google.common.hash.HashCode;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.RootLocation;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public final class RootCore extends JetCore {
  public final Object payload;

  public RootCore(String name,
                  Object payload,
                  HashCode[] hashes,
                  JetArm[] arms,
                  JetHook[] hooks,
                  ChildCore[] children) {
    super(name, hashes, arms, hooks, children);
    this.payload = payload;
  }

  @Override
  protected Location getLocation(Location parent, Map<String,Hook> hooks) {
    assert( null == parent );
    return new RootLocation(name, hooks, payload);
  }
}
