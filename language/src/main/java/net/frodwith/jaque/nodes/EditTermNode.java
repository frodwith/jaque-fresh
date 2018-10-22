package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class EditTermNode extends EditPartNode {
  private @Child NockExpressionNode valueNode;

  public EditTermNode(NockExpressionNode valueNode) {
    this.valueNode = valueNode;
  }

  public final Object executeEdit(VirtualFrame frame, Object large) {
    return valueNode.executeGeneric(frame);
  }
}
