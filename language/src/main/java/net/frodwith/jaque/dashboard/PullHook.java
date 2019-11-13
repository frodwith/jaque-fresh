package net.frodwith.jaque.dashboard;

import java.util.Objects;

import net.frodwith.jaque.data.Axis;

public final class PullHook extends Hook {
  public final Axis toSubject;
  public final Axis arm;

  public PullHook(Axis toSubject, Axis arm) {
    this.toSubject = toSubject;
    this.arm = arm;
  }

  @Override
  public int hashCode() {
    return Objects.hash(toSubject, arm);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PullHook)) {
      return false;
    }

    return toSubject.equals(((PullHook)o).toSubject) &&
           arm.equals(((PullHook)o).arm);
  }
}
