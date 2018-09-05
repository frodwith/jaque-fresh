package net.frodwith.jaque.exception;

public final class AtomRequiredException extends RequireException {
  public AtomRequiredException(Object o) {
    super("Atom", o);
  }
}
