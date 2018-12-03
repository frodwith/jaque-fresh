package net.frodwith.jaque.dashboard;

import java.util.Map;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.FastClue;

public final class StaticChildLocation extends StaticLocation {
  public final StaticLocation parent;

  public StaticChildLocation(String name,
                             Map<String,Hook> hooks,
                             StaticLocation parent) {
    super(name, hooks);
    this.parent = parent;
  }

  @Override
  public void register(Registration registration) {
    registration.registerChild(Axis.TAIL, this, parent);
  }

  @Override
  public void audit(FastClue clue) {
    if ( Axis.TAIL != clue.toParent ) {
      LOG.warning(name + " axis mismatch: got " + clue.toParent.toString() +
          ", expected 3.");
    }
    super.audit(clue);
  }
}
