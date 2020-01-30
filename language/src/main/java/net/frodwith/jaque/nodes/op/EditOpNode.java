package net.frodwith.jaque.nodes.op;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.nodes.NockNode;

public abstract class EditOpNode extends NockNode {
  public abstract Object executeEdit(Object whole, Object part);

  public static EditOpNode fromAxis(Axis axis, Dashboard dashboard) {
    return axis.isIdentity()
      ? new EditTermOpNode()
      : axis.inHead()
      ? new EditHeadOpNode(axis.mas(), dashboard)
      : new EditTailOpNode(axis.mas(), dashboard);
  }
}
