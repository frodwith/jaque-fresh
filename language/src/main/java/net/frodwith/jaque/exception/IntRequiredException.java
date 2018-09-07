package net.frodwith.jaque.exception;

public final class IntRequiredException extends RequireException {
  public IntRequiredException(Object o) {
    super("int", o);
  }
}
