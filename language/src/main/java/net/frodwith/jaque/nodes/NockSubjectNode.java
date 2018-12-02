package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

public abstract class NockSubjectNode extends NockNode {
  public abstract Object executeGeneric(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectLong(executeGeneric(frame));
  }

  public BigAtom executeBigAtom(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectBigAtom(executeGeneric(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectCell(executeGeneric(frame));
  }
}
