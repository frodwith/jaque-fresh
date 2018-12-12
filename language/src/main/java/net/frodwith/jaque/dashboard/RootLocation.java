package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.Objects;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;

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
  public boolean equals(Object o) {
    if ( !(o instanceof RootLocation) ) {
      return false;
    }
    RootLocation rl = (RootLocation) o;
    return name.equals(rl.name) 
      && hooks.equals(rl.hooks)
      && Equality.equals(payload, rl.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, hooks) ^ Mug.get(payload);
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
