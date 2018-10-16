package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockFunction;

public final class HeadEvalNode extends NockEvalNode {
  @Child private NockFunctionDispatchNode dispatchNode;

  public HeadEvalNode(NockFunctionLookupNode lookupNode,
                        NockExpressionNode subjectNode) {
    super(lookupNode, subjectNode);
    this.dispatchNode = NockFunctionDispatchNodeGen.create();
  }

  @Override
  public final Object dispatch(NockFunction function, Object subject) {
    return dispatchNode.executeFunction(function, subject);
  }
}
