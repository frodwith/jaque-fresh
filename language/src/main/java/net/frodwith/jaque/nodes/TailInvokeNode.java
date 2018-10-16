package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.exception.NockControlFlowException;

public final class TailInvokeNode extends NockInvokeNode {

  public TailInvokeNode(Object armAxis, NockObjectLookupNode lookupNode,
      ContextReference<NockContext> contextReference) {
    super(armAxis, lookupNode, contextReference);
  }

  public final Object dispatch(NockFunction function, Object subject) {
    throw new NockControlFlowException(function, subject);
  }
}
