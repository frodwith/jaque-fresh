package net.frodwith.jaque.jet;

import java.util.List;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.BatteryHash;

public abstract class JetCore {
  public final String name;
  public final List<BatteryHash> hashes;
  public final List<JetArm> arms;
  public final List<JetHook> hooks;
  public final List<ChildCore> children;

  protected abstract Location getLocation(Location parent,
                                          Map<String,Hook> hooks);

  public JetCore(String name,
                 List<BatteryHash> hashes,
                 List<JetArm> arms,
                 List<JetHook> hooks,
                 List<ChildCore> children) {
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
