
package net.frodwith.jaque.exception;

import com.oracle.truffle.api.nodes.SlowPathException;

public class FailException extends SlowPathException {
  public FailException(String message) {
    super(message);
  }

  protected FailException(String message, Throwable cause) {
    super(message, cause);
  }
}
