package net.frodwith.jaque.exception;

public final class CellRequiredException extends RequireException {
  public CellRequiredException(Object o) {
    super("Cell", o);
  }
}
