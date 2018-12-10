package net.frodwith.jaque.jet;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import us.bpsm.edn.Keyword;

import net.frodwith.jaque.NockLanguage;
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
                              Map<BatteryHash,Registration> hot,
                              Map<Location,AxisMap<NockFunction>> driver) {
    Map<String,Hook> hookMap = new HashMap<>();
    for ( JetHook h : hooks ) {
      hookMap.put(h.name, h.hook);
    }

    Registration r = new Registration();
    Location loc = getLocation(parent, hookMap);
    loc.register(r);
    for ( BatteryHash h : hashes ) {
      hot.put(h, r);
    }

    AxisMap functions = AxisMap.EMPTY;
    for ( JetArm a : arms ) {
      functions = functions.insert(a.getAxis(hookMap), a.getFunction(language));
    }
    driver.put(loc, functions);

    for ( ChildCore child : children ) {
      child.addToMaps(loc, language, hot, driver);
    }
  }

  protected static BatteryHash[] parseHashes(Object val) {
    if ( !(val instanceof List) ) {
      return new BatteryHash[0];
    }
    else {
      List<BatteryHash> list = new ArrayList<>();
      for ( Object o : (List<?>) val ) {
        list.add(BatteryHash.parseOption(o));
      }
      return list.toArray(new BatteryHash[list.size()]);
    }
  }

  protected static JetArm[] parseArms(Object val) {
    if ( !(val instanceof List) ) {
      return new JetArm[0];
    }
    else {
      List<JetArm> list = new ArrayList<>();
      for ( Object o : (List<?>) val ) {
        list.add(JetArm.parseOption(o));
      }
      return list.toArray(new JetArm[list.size()]);
    }
  }

  protected static JetHook[] parseHooks(Object val) {
    if ( !(val instanceof List) ) {
      return new JetHook[0];
    }
    else {
      List<JetHook> list = new ArrayList<>();
      for ( Object o : (List<?>) val ) {
        list.add(JetHook.parseOption(o));
      }
      return list.toArray(new JetHook[list.size()]);
    }
  }

  protected static ChildCore[] parseChildren(Object val) {
    if ( !(val instanceof List) ) {
      return new ChildCore[0];
    }
    else {
      List<ChildCore> list = new ArrayList<>();
      for ( Object o : (List<?>) val ) {
        list.add(ChildCore.parseOption(o));
      }
      return list.toArray(new ChildCore[list.size()]);
    }
  }
}
