package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class TossNode extends NockExpressionNode {
  @Child private NockExpressionNode tossNode;
  @Child private NockExpressionNode valueNode;

  public TossNode(NockExpressionNode tossNode, NockExpressionNode valueNode) {
    this.tossNode = tossNode;
    this.valueNode = valueNode;
  }

  public Object executeGeneric(VirtualFrame frame) {
    tossNode.executeGeneric(frame);
    return valueNode.executeGeneric(frame);
  }
}
