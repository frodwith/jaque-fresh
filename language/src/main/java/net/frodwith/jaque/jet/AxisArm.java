package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.function.BiFunction;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.runtime.NockContext;

public final class AxisArm extends JetArm {
  private final Axis axis;

  public AxisArm(Axis axis,
    BiFunction<ContextReference<NockContext>, Axis, SubjectNode> factory) {
    super(factory);
    this.axis = axis;
  }

  @Override
  public Axis getAxis(Map<String,Hook> hooks) {
    return axis;
  }
}
