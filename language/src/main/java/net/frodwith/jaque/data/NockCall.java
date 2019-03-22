package net.frodwith.jaque.data;

import com.oracle.truffle.api.CallTarget;

public final class NockCall {
  public final CallTarget function;
  public final Object subject;

  public NockCall(CallTarget function, Object subject) {
    this.function = function;
    this.subject = subject;
  }
}
