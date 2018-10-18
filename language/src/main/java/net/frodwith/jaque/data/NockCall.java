package net.frodwith.jaque.data;

public final class NockCall {
  public final NockFunction function;
  public final Object subject;

  public NockCall(NockFunction function, Object subject) {
    this.function = function;
    this.subject = subject;
  }
}
