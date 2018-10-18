package net.frodwith.jaque.exception;

import com.oracle.truffle.api.nodes.SlowPathException;

public final class ExitException extends SlowPathException {
  // Corresponds to a hoon !!, a nock [0 0], a u3m_bail(c3__exit).
  // Should be thrown by runtime code.

  // Truffle nodes which call runtime code that throw this exception
  // should handle it and throw a NockException.

  // If you generate a stacktrace after catching this exception and there is a
  // non-empty message, you should include it in the stacktrace.
  public ExitException(String message) {
    super(message);
  }
}
