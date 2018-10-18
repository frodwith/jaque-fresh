package net.frodwith.jaque.exception;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

// Intended to be thrown by language nodes when runtime code has thrown an
// ExitExcption.
public final class NockException extends RuntimeException implements TruffleException {
  private final Node location;

  @TruffleBoundary
  public NockException(String message, Node location) {
    super(message);
    this.location = location;
  }

  @TruffleBoundary
  public NockException(String message, Throwable cause, Node location) {
    super(message, cause);
    this.location = location;
  }

  @Override
  public Throwable fillInStackTrace() {
    return null;
  }

  @Override
  public Node getLocation() {
    return location;
  }
}
