package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.NockExpressionNode;

@NodeInfo(shortName = "1")
public final class LiteralCellExpressionNode extends NockExpressionNode {
  private final Cell value;

  public LiteralCellExpressionNode(Cell value) {
    this.value = value;
  }

  @Override
  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return value;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return value;
  }
}
