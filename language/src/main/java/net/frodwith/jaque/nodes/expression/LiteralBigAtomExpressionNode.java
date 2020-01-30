package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.NockExpressionNode;

@NodeInfo(shortName = "1")
public final class LiteralBigAtomExpressionNode extends NockExpressionNode {
  private final BigAtom value;

  public LiteralBigAtomExpressionNode(BigAtom value) {
    this.value = value;
  }

  @Override
  public BigAtom executeBigAtom(VirtualFrame frame) throws UnexpectedResultException {
    return value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }
}
