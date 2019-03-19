package net.frodwith.jaque.jet;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockContext;

public final class JetContext {
  public final Axis axis;
  public final Dashboard dashboard;
  public final NockLanguage language;

  public JetContext(Axis axis, Dashboard dashboard, NockLanguage language) {
    this.axis = axis;
    this.dashboard = dashboard;
    this.language = language;
  }
}
