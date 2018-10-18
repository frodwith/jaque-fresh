package net.frodwith.jaque.exception;

import com.oracle.truffle.api.nodes.ControlFlowException;

import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.NockCall;

public final class NockControlFlowException extends ControlFlowException {
  public final NockCall call;

  public NockControlFlowException(NockCall call) {
    this.call = call;
  }
}
