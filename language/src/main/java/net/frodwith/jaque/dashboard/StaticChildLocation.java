package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.Objects;

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
  public boolean equals(Object o) {
    if ( !(o instanceof StaticChildLocation) ) {
      return false;
    }
    StaticChildLocation scl = (StaticChildLocation) o;

    return name.equals(scl.name)
      && hooks.equals(scl.hooks)
      && parent.equals(scl.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, hooks, parent);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("StaticChildLocation{name=");
    b.append(this.name.toString());
    b.append(", hooks=");
    b.append(this.hooks.toString());
    b.append(", parent=");
    b.append(this.parent.toString());
    b.append("}");
    return b.toString();
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
