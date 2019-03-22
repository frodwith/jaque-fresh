package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class NockHeadCallNode extends NockExpressionNode {
  @Child private NockCallLookupNode lookupNode;
  @Child private NockCallDispatchNode dispatchNode;

  public NockHeadCallNode(NockCallLookupNode lookupNode) {
    this.lookupNode = lookupNode;
    this.dispatchNode = NockCallDispatchNodeGen.create();
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return dispatchNode.executeCall(lookupNode.executeLookup(frame));
  }
}
