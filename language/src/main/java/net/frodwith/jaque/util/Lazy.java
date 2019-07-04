package net.frodwith.jaque.util;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class Lazy<T> {
  private @CompilationFinal T value;
  private final Supplier<T> compute;

  public Lazy(Supplier<T> compute) {
    this.compute = compute;
    this.value = null;
  }

  public T get() {
    if ( null == value ) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      value = compute.get();
    }
    return value;
  }
}
