package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

@NodeInfo(shortName = "1")
public final class LiteralLongExpressionNode extends NockExpressionNode {
  private final long value;

  public LiteralLongExpressionNode(long value) {
    this.value = value;
  }

  @Override
  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }
}
