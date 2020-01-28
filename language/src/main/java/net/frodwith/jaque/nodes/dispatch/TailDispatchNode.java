package net.frodwith.jaque.nodes.dispatch;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.exception.NockControlFlowException;

public final class TailDispatchNode extends DispatchNode {
  public Object executeDispatch(CallTarget target, Object subject) {
    throw new NockControlFlowException(target, subject);
  }
}
