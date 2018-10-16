package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.exception.NockControlFlowException;

public final class TailEvalNode extends NockEvalNode {
  public TailEvalNode(NockFunctionLookupNode lookupNode,
                      NockExpressionNode subjectNode) {
    super(lookupNode, subjectNode);
  }

  @Override
  public final Object dispatch(NockFunction function, Object subject) {
    throw new NockControlFlowException(function, subject);
  }
}
