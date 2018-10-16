package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockContext;

public final class HeadInvokeNode extends NockInvokeNode {
  @Child private NockFunctionDispatchNode dispatchNode;

  public HeadInvokeNode(Object armAxis, NockObjectLookupNode lookupNode,
      ContextReference<NockContext> contextReference) { 
    super(armAxis, lookupNode, contextReference);
    this.dispatchNode = NockFunctionDispatchNodeGen.create();
  }

  public final Object dispatch(NockFunction function, Object subject) {
    return dispatchNode.executeFunction(function, subject);
  }
}
