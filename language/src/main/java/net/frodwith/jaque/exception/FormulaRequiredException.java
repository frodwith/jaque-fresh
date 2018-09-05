package net.frodwith.jaque.exception;

public final class FormulaRequiredException extends RequireException {
  public FormulaRequiredException(Object value) {
    super("Formula", value);
  }

  public FormulaRequiredException(Object value, Throwable cause) {
    super("Formula", value, cause);
  }
}
