package net.frodwith.jaque.exception;

public final class NeedException extends RuntimeException {
  private final Object path;

  public NeedException(Object path) {
    this.path = path;
  }

  public Object getPath() {
    return path;
  }
}
