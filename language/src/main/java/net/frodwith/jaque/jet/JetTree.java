package net.frodwith.jaque.jet;

import java.util.Map;

import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public final class JetTree {
  private final RootCore[] roots;

  public JetTree(RootCore[] roots) {
    this.roots = roots;
  }

  public void addToMaps(Map<BatteryHash,Registration> hot,
                        Map<Location, AxisMap<NockFunction>> driver) {
    for ( RootCore r : roots ) {
      r.addToMaps(hot, driver);
    }
  }
}
