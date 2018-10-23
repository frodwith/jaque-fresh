package net.frodwith.jaque.data;

import java.util.Map;

public final class FastClue {
  public final String name;
  public final Axis toParent;
  public final Map<String, Hook> hooks;
  public final Cell noun;

  public FastClue(Cell noun, String name, Axis toParent, Map<String,Hook> hooks) {
    this.noun = noun;
    this.name = name;
    this.toParent = toParent;
    this.hooks = hooks;
  }

  public static FastClue parse(Object clue) {
    // XX todo
    return null;
  }

  public static abstract class Hook {
  }

  public static final class FragHook extends Hook {
    public final Axis axis;

    public FragHook(Axis axis) {
      this.axis = axis;
    }
  }

  public static final class KickHook extends Hook {
    public final Axis toSubject;
    public final Axis arm;

    public KickHook(Axis toSubject, Axis arm) {
      this.toSubject = toSubject;
      this.arm = arm;
    }
  }
}
