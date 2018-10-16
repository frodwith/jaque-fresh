package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockFunction;

public abstract class NockEvalNode extends NockExpressionNode {
  @Child protected NockFunctionLookupNode lookupNode;
  @Child protected NockExpressionNode subjectNode;

  protected abstract Object dispatch(NockFunction function, Object subject);

  protected NockEvalNode(NockFunctionLookupNode lookupNode,
                         NockExpressionNode subjectNode) {
    this.lookupNode = lookupNode;
    this.subjectNode = subjectNode;
  }

  @Override
  public final Object executeGeneric(VirtualFrame frame) {
    Object subject = subjectNode.executeGeneric(frame);
    NockFunction f = lookupNode.executeLookup(frame);
    return dispatch(f, subject);
  }
}
