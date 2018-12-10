package net.frodwith.jaque.jet;

import java.util.Map;

import us.bpsm.edn.Keyword;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.FragHook;
import net.frodwith.jaque.dashboard.PullHook;

public final class JetHook {
  public final String name;
  public final Hook hook;
  private static final Keyword fragKey = Keyword.newKeyword("fragment"),
                               nameKey = Keyword.newKeyword("name"),
                               armKey  = Keyword.newKeyword("arm");

  public JetHook(String name, Hook hook) {
    this.name = name;
    this.hook = hook;
  }

  public static JetHook parseOption(Object option) {
    Map<?,?> m = (Map<?,?>) option;
    String name = (String) m.get(nameKey);
    Object armO = m.get(armKey);
    Object fragO = m.get(fragKey);
    Axis fragA = Axis.parseOption(m.get(fragKey));

    if ( null == fragA ) {
      fragA = Axis.IDENTITY;
    }

    Hook hook = ( null == armO )
              ? new FragHook(fragA)
              : new PullHook(Axis.parseOption(armO), fragA);

    return new JetHook(name, hook);
  }
}
