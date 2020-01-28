package net.frodwith.jaque.nodes.dispatch;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;

import net.frodwith.jaque.exception.NockControlFlowException;

public final class DirectDispatchNode extends NockNode {
  private @Child DirectCallNode callNode;
  private @Child HeadDispatchNode nextDispatch;
  private final BranchProfile tailProfile, nonLoopProfile;
  private final CallTarget target;

  public DirectDispatchNode(CallTarget target) {
    this.target = target;
    this.callNode = DirectCallNode.create(target);
    this.nextDispatch = null;
    this.tailProfile = BranchProfile.create();
    this.nonLoopProfile = BranchProfile.create();
  }

  public Object executeDirect(Object subject) {
    while ( true ) {
      try {
        return callNode.call(subject);
      }
      catch ( NockControlFlowException e ) {
        tailProfile.enter();
        if ( target == e.target ) {
          subject = e.subject;
        }
        else {
          nonLoopProfile.enter();
          if ( null == nextDispatch ) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            nextDispatch = HeadDispatchNodeGen.create();
          }
          return nextDispatch.executeDispatch(e.target, e.subject);
        }
      }
    }
  }
}
