package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.op.SlotOpNode;
import net.frodwith.jaque.nodes.NockExpressionNode;

public final class SlotExpressionNode extends NockExpressionNode {
  @Child private SlotOpNode opNode;

  public SlotExpressionNode(Axis axis) {
    this.opNode = SlotOpNode.fromAxis(axis);
  }

  public Object executeGeneric(VirtualFrame frame) {
    return opNode.executeSlot(NockLanguage.getSubject(frame));
  }
}
