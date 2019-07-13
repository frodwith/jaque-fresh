package net.frodwith.jaque.util;

import java.util.function.Supplier;
import java.util.Optional;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class Lazy<T> {
  @CompilationFinal private Optional<T> value;
  @CompilationFinal private Supplier<T> compute;

  public Lazy(Supplier<T> compute) {
    this.compute = compute;
    this.value = Optional.empty();
  }

  public boolean computed() {
    return value.isPresent();
  }

  public T get() {
    if ( value.isPresent() ) {
      return value.get();
    }
    else {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      T computed = compute.get();
      value = Optional.of(computed);
      compute = null;
      return t;
    }
  }
}
