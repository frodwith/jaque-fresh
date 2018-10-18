package net.frodwith.jaque.exception;

public final class FailError extends Error {
  // FailErrors should be thrown when execution cannot continue (for example,
  // because of limitations of atom size) even though nock would have contined
  // without a !! (Exit bail)
  public FailError(String message) {
    super(message);
  }
}
