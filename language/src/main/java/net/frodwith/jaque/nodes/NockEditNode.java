package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

public final class NockEditNode extends NockExpressionNode {
  private @Child NockExpressionNode largeNode;
  private @Child EditPartNode editNode;
  private final Axis editAxis;

  public NockEditNode(NockExpressionNode largeNode,
                      EditPartNode editNode,
                      Axis editAxis) {
    this.largeNode = largeNode;
    this.editNode = editNode;
    this.editAxis = editAxis;
  }

  @Override
  public Cell executeCell(VirtualFrame frame) {
    Cell large;
    try {
      large = largeNode.executeCell(frame);
    }
    catch ( UnexpectedResultException e ) {
      throw new NockException("edit atom", e, this);
    }
    Object product = editNode.executeEdit(frame, large);
    Cell pc;
    try {
      pc = Cell.require(product);
    }
    catch (ExitException e) {
      CompilerDirectives.transferToInterpreter();
      throw new AssertionError();
    }
    pc.copyObject(large, editAxis);
    return pc;
  }

  @Override
  public Cell executeGeneric(VirtualFrame frame) {
    return executeCell(frame);
  }
}
