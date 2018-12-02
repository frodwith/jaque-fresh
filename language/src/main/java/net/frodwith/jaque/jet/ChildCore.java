package net.frodwith.jaque.jet;

public final class ChildCore extends JetCore {
  private final Axis toParent;

  public ChildCore(String name,
                   Axis toParent,
                   List<BatteryHash> hashes,
                   List<JetArm> arms,
                   List<JetHook> hooks,
                   List<JetCore> children) {
    super(name, hashes, arms, hooks, children);
    this.toParent = toParent;
  }

  @Override
  protected Location getLocation(Location parent, Map<String,Hook> hooks) {
    assert( null != parent );
    return ( Axis.TAIL == toParent && parent instanceof StaticLocation )
      ? new StaticChildLocation(name, hooks, (StaticLocation) parent)
      : new DynamicChildLocation(name, hooks, parent, toParent);
  }
}
