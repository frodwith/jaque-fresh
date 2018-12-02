package net.frodwith.jaque.jet;

public final class JetTree {
  private final List<RootCore> roots;

  public JetTree(List<RootCore> roots) {
    this.roots = roots;
  }

  public void addToMaps(Map<BatteryHash,Registration hotMap,
                        Map<Location, AxisMap<NockFunction>> driverMap) {
    for ( RootCore r : roots ) {
      r.addToMaps(hotMap, driverMap);
    }
  }
}
