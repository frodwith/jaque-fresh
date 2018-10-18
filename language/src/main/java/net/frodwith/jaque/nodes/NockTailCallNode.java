package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.exception.NockControlFlowException;

public final class NockTailCallNode extends NockExpressionNode {
  @Child private NockCallLookupNode lookupNode;

  public NockTailCallNode(NockCallLookupNode lookupNode) {
    this.lookupNode = lookupNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    throw new NockControlFlowException(lookupNode.executeLookup(frame));
  }
}
