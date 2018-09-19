package net.frodwith.jaque.exception;

import com.oracle.truffle.api.nodes.ControlFlowException;

import net.frodwith.jaque.runtime.NockFunction;

public final class NockControlFlowException extends ControlFlowException {
  public final NockFunction function;
  public final Object subject;

  public NockControlFlowException(NockFunction function, Object subject ) {
    this.function = function;
    this.subject = subject;
  }
}
