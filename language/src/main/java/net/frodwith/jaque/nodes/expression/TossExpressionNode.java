package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class TossExpressionNode extends NockExpressionNode {
  @Child private NockExpressionNode tossNode;
  @Child private NockExpressionNode valueNode;

  public TossExpressionNode(NockExpressionNode tossNode,
                            NockExpressionNode valueNode) {
    this.tossNode = tossNode;
    this.valueNode = valueNode;
  }

  public Object executeGeneric(VirtualFrame frame) {
    tossNode.executeGeneric(frame);
    return valueNode.executeGeneric(frame);
  }
}
