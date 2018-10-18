package net.frodwith.jaque.nodes.call;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.exception.NockControlFlowException;

public final class TailInvokeNode extends NockExpressionNode {
  @Child private NockFunctionLookupNode lookupNode;
  @Child private NockExpressionNode subjectNode;

  public TailInvokeNode(NockFunctionLookupNode lookupNode,
                        NockExpressionNode subjectNode) {
    this.lookupNode = lookupNode;
    this.subjectNode = subjectNode;
  }

  public final Object executeGeneric(VirtualFrame frame) {
    Object subject = subjectNode.executeGeneric(frame);
    NockFunction f = lookupNode.executeLookup(frame);
    throw new NockControlFlowException(f, subject);
  }
}
