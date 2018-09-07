package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;

@NodeInfo(shortName = "1")
public final class LiteralCellNode extends NockExpressionNode {
  private final Cell value;

  public LiteralCellNode(Cell value) {
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
