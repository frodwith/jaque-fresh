package net.frodwith.jaque.nodes.op;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.nodes.NockNode;

public final class SlamOpNode extends NockNode {
  private @Child EditOpNode editNode;
  private @Child PullOpNode pullNode;

  public SlamOpNode(AstContext astContext, boolean tailPosition) {
    this.editNode = EditOpNode.fromAxis(Axis.SAMPLE, astContext.dashboard);
    this.pullNode = PullOpNodeGen.create(astContext, Axis.HEAD, tailPosition);
  }

  public Object executeSlam(Object gate, Object sample) {
    return pullNode.executePull(editNode.executeEdit(gate, sample));
  }
}
