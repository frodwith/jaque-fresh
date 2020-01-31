package net.frodwith.jaque.exception;

// Thrown when the nock code for a scry crashes, containing crash
public final class MetaException extends RuntimeException {
  public final NockException cause;

  public MetaException(NockException cause) {
    this.cause = cause;
  }
}
