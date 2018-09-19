package net.frodwith.jaque.nodes.call;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.runtime.NockFunction;

public final class HeadInvokeNode extends NockExpressionNode {
  @Child private NockFunctionDispatchNode dispatchNode;
  @Child private NockFunctionLookupNode lookupNode;
  @Child private NockExpressionNode subjectNode;

  public HeadInvokeNode(NockFunctionLookupNode lookupNode,
                        NockExpressionNode subjectNode) {
    this.lookupNode = lookupNode;
    this.subjectNode = subjectNode;
    this.dispatchNode = NockFunctionDispatchNodeGen.create();
  }

  public final Object executeGeneric(VirtualFrame frame) {
    Object subject = subjectNode.executeGeneric(frame);
    NockFunction f = lookupNode.executeLookup(frame);
    return dispatchNode.executeFunction(f, subject);
  }
}
