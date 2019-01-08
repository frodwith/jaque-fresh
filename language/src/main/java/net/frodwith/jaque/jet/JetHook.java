package net.frodwith.jaque.jet;

import java.util.Map;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.FragHook;
import net.frodwith.jaque.dashboard.PullHook;

public final class JetHook {
  public final String name;
  public final Hook hook;

  public JetHook(String name, Hook hook) {
    this.name = name;
    this.hook = hook;
  }
}
