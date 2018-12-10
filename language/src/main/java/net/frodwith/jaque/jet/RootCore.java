package net.frodwith.jaque.jet;

import java.util.Map;

import us.bpsm.edn.Keyword;

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
  private static final Keyword payloadKey  = Keyword.newKeyword("payload");

  public RootCore(String name,
                  Object payload,
                  BatteryHash[] hashes,
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

  public void addToMaps(NockLanguage language,
                        Map<BatteryHash,Registration> hotMap,
                        Map<Location,AxisMap<NockFunction>> driverMap) {
    addToMaps(null, language, hotMap, driverMap);
  }

  public static RootCore parseOption(Object option) {
    Map<?,?> m = (Map<?,?>) option;
    String name = (String) m.get(nameKey);
    Object payload = Atom.parseOption(m.get(payloadKey));

    BatteryHash[] hashes = parseHashes(m.get(hashKey));
    JetArm[] arms = parseArms(m.get(armKey));
    JetHook[] hooks = parseHooks(m.get(hookKey));
    ChildCore[] children = parseChildren(m.get(childKey));

    return new RootCore(name, payload, hashes, arms, hooks, children);
  }
}
