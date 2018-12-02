package net.frodwith.jaque.jet;

public final class RootCore extends JetCore {
  private final Object payload;

  public RootCore(String name,
                  Object payload,
                  List<BatteryHash> hashes,
                  List<JetArm> arms,
                  List<JetHook> hooks,
                  List<JetCore> children) {
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
