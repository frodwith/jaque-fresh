package net.frodwith.jaque.jet;

import java.util.Map;

import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public final class RootCore extends JetCore {
  private final Object payload;

  public RootCore(String name,
                  Object payload,
                  BatteryHash[] hashes,
                  JetArm[] arms,
                  JetHook[] hooks,
                  JetCore[] children) {
    super(name, hashes, arms, hooks, children);
    this.payload = payload;
  }

  @Override
  protected Location getLocation(Location parent, Map<String,Hook> hooks) {
    assert( null == parent );
    return new RootLocation(name, hooks, payload);
  }

  public void addToMaps(Map<BatteryHash,Registration> hotMap,
                        Map<Location,AxisMap<NockFunction>> driverMap) {
    addToMaps(null, hotMap, driverMap);
  }
}
