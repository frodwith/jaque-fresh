package net.frodwith.jaque.dashboard;

import java.util.Objects;

import net.frodwith.jaque.data.Axis;

public final class FragHook extends Hook {
  public final Axis axis;

  public FragHook(Axis axis) {
    this.axis = axis;
  }

  @Override
  public int hashCode() {
    return axis.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FragHook)) {
      return false;
    }

    return axis.equals(((FragHook)o).axis);
  }
}
