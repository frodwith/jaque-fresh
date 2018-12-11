package net.frodwith.jaque.jet;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import us.bpsm.edn.Keyword;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public abstract class JetCore {
  public final String name;
  public final BatteryHash[] hashes;
  public final JetArm[] arms;
  public final JetHook[] hooks;
  public final ChildCore[] children;
  protected static final Keyword nameKey  = Keyword.newKeyword("name"),
                                 hashKey  = Keyword.newKeyword("hashes"),
                                 armKey   = Keyword.newKeyword("arms"),
                                 hookKey  = Keyword.newKeyword("hooks"),
                                 childKey = Keyword.newKeyword("children");

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
                              NockLanguage language,
                              NockContext context,
                              Map<BatteryHash,Registration> hot,
                              Map<Location,AxisMap<NockFunction>> driver) {
    Map<String,Hook> hookMap = new HashMap<>();
    for ( JetHook h : hooks ) {
      hookMap.put(h.name, h.hook);
    }

    Registration r = new Registration(context);
    Location loc = getLocation(parent, hookMap);
    loc.register(r);
    for ( BatteryHash h : hashes ) {
      hot.put(h, r);
    }

    AxisMap functions = AxisMap.EMPTY;
    for ( JetArm arm : arms ) {
      Axis ax = arm.getAxis(hookMap);
      functions = functions.insert(ax, arm.getFunction(language, ax));
    }
    driver.put(loc, functions);

    for ( ChildCore child : children ) {
      child.addToMaps(loc, language, context, hot, driver);
    }
  }
}
