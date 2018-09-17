package net.frodwith.jaque.runtime;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;

public final class NockContext {
  private final Env env;

  public NockContext(NockLanguage language, Env env) {
    this.env = env;
  }

  public static Object fromForeignValue(Object a) {
    if ( a instanceof Long || a instanceof BigAtom || a instanceof Cell ) {
      return a;
    }
    else if ( a instanceof Character ) {
      return (long) ((Character) a).charValue();
    }
    else if ( a instanceof Number ) {
      return fromForeignNumber(a);
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new IllegalArgumentException(a + " is not a noun");
    }
  }

  @TruffleBoundary
  private static long fromForeignNumber(Object a) {
    return ((Number) a).longValue();
  }
}
