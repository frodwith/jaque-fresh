package net.frodwith.jaque.nodes.dispatch;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;

import net.frodwith.jaque.exception.NockControlFlowException;

public final class IndirectDispatchNode extends NockNode {
  private @Child IndirectCallNode callNode;
  private final BranchProfile tailProfile;

  public IndirectDispatchNode() {
    this.callNode = IndirectCallNode.create();
    this.tailProfile = BranchProfile.create();
  }

  public Object executeIndirect(CallTarget target, Object subject) {
    while ( true ) {
      try {
        return callNode.call(target, subject);
      }
      catch ( NockControlFlowException e ) {
        tailProfile.enter();
        target = e.target;
        subject = e.subject;
      }
    }
  }
}
