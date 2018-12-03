package net.frodwith.jaque.dashboard;

import java.util.Map;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.FastClue;

public final class RootLocation extends StaticLocation {
  public final Object payload;

  public RootLocation(String name, Map<String,Hook> hooks, Object payload) {
    super(name, hooks);
    this.payload = payload;
  }

  @Override
  public void register(Registration registration) {
    registration.registerRoot(payload, this);
  }

  @Override
  public void audit(FastClue clue) {
    if ( !clue.toParent.isCrash() ) {
      LOG.warning(name + " axis mismatch: got " + clue.toParent.toString() +
          ", expected 0.");
    }
    super.audit(clue);
  }
}
