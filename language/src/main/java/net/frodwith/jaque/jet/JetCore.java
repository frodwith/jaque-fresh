package net.frodwith.jaque.jet;

import java.util.Map;

import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public abstract class JetCore {
  protected final String name;
  protected final BatteryHash[] hashes;
  protected final JetArm[] arms;
  protected final JetHook[] hooks;
  protected final ChildCore[] children;

  protected abstract Location getLocation(Location parent,
                                          Map<String,Hook> hooks);

  protected JetCore(String name,
                    BatteryHash[] hashes,
                    JetArm[] arms,
                    JetHook[] hooks,
                    ChildCore[] children) {
    this.name     = name;
    this.hashes   = hashes;
    this.arms     = arms;
    this.hooks    = hooks;
    this.children = children;
  }

  public final void addToMaps(Location parent,
                              Map<BatteryHash,Registration> hot,
                              Map<Location,AxisMap<NockFunction>> driver) {
    Map<String,Hook> hookMap = new HashMap<>();
    for ( JetHook h : hooks ) {
      h.put(h.getName(), h.getHook());
    }

    Registration r = new Registration();
    Location loc = getLocation(parent, hookMap);
    loc.register(r);
    for ( BatteryHash h : hashes ) {
      hot.put(h, r);
    }

    AxisMap functions = AxisMap.EMPTY;
    for ( JetArm a : arms ) {
      functions = functions.insert(a.getAxis(hookMap), a.getFunction());
    }
    driver.put(loc, functions);

    for ( ChildCore child : children ) {
      child.addToMaps(loc, hot, driver);
    }
  }
}
