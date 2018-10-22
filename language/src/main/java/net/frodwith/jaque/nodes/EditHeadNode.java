package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

public final class EditHeadNode extends EditPartNode {
  private @Child EditPartNode nextEditNode;

  public EditHeadNode(EditPartNode nextEditNode) {
    this.nextEditNode = nextEditNode;
  }

  public final Cell executeEdit(VirtualFrame frame, Object large) {
    Cell c;
    try {
      c = Cell.require(large);
    }
    catch (ExitException e) {
      throw new NockException("edit atom part", e, this);
    }
    Object newHead = nextEditNode.executeEdit(frame, c.head);
    return new Cell(newHead, c.tail);
  }
}
