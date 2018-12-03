package net.frodwith.jaque.jet;

import net.frodwith.jaque.dashboard.Hook;

public final class JetHook {
  public final String name;
  public final Hook hook;

  public JetHook(String name, Hook hook) {
    this.name = name;
    this.hook = hook;
  }
}
