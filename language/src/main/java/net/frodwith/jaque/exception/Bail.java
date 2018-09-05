package net.frodwith.jaque.exception;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

public final class Bail extends RuntimeException implements TruffleException {
  private final Node location;

  @TruffleBoundary
  public Bail(String message, Node location) {
    super(message);
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
