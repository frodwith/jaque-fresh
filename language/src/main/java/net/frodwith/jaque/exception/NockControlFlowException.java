package net.frodwith.jaque.exception;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

public final class NockControlFlowException extends ControlFlowException {
  public final CallTarget target;
  public final Object subject;

  public NockControlFlowException(CallTarget target, Object subject) {
    this.target = target;
    this.subject = subject;
  }
}
