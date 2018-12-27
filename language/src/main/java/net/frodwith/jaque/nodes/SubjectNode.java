package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

import net.frodwith.jaque.exception.NockException;

public abstract class SubjectNode extends NockNode {
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

  // Specializations for Atoms must often take Object arguments, so should use
  // this helper method to ensure they got one.
  protected Object needAtom(Object a) {
    if ( a instanceof Long || a instanceof BigAtom ) {
      return a;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new NockException("cell where atom required", this);
    }
  }
}
