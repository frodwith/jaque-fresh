package net.frodwith.jaque.dashboard;

import net.frodwith.jaque.data.Axis;

public final class PullHook extends Hook {
  public final Axis toSubject;
  public final Axis arm;

  public PullHook(Axis toSubject, Axis arm) {
    this.toSubject = toSubject;
    this.arm = arm;
  }
}
