package net.frodwith.jaque.exception;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

public final class NeedException extends RuntimeException implements TruffleException {
  private final Node location;
  private final Object path;

  @TruffleBoundary
  public NeedException(Object path, Node location) {
    super("%need");
    this.path = path;
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

  public Object getPath() {
    return path;
  }
}
