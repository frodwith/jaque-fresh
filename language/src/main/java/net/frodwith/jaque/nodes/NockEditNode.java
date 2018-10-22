package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

public final class NockEditNode extends NockExpressionNode {
  private @Child NockExpressionNode largeNode;
  private @Child EditPartNode editNode;

  public NockEditNode(NockExpressionNode largeNode, EditPartNode editNode) {
    this.largeNode = largeNode;
    this.editNode = editNode;
  }

  @Override
  public Cell executeCell(VirtualFrame frame) {
    Cell large;
    try {
      large = largeNode.executeCell(frame);
    }
    catch ( UnexpectedResultException e ) {
      throw new NockException("edit cell", e, this);
    }
    Object product = editNode.executeEdit(frame, large);
    try {
      return Cell.require(product);
    }
    catch (ExitException e) {
      throw new AssertionError();
    }
  }

  @Override
  public Cell executeGeneric(VirtualFrame frame) {
    return executeCell(frame);
  }
}
