package net.frodwith.jaque.jet;

import java.util.Map;

import com.oracle.truffle.api.dsl.NodeFactory;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.dashboard.Hook;

public final class AxisArm extends JetArm {
  private final Axis axis;

  protected AxisArm(Axis axis, NodeFactory<? extends SubjectNode> factory) {
    super(factory);
    this.axis = axis;
  }

  @Override
  public Axis getAxis(Map<String,Hook> hooks) {
    return axis;
  }
}
