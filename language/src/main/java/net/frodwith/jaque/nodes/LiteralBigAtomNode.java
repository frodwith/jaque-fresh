package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.BigAtom;

@NodeInfo(shortName = "1")
public final class LiteralBigAtomNode extends NockExpressionNode {
  private final BigAtom value;

  public LiteralBigAtomNode(BigAtom value) {
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
