package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;

import net.frodwith.jaque.exception.Fail;

public final class NockContext {
  private final Env env;
  private final NockLanguage language;
  public final NockFunctionRegistry functionRegistry;
  public final Dashboard dashboard;

  public NockContext(NockLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.dashboard = new Dashboard();
    this.functionRegistry = new NockFunctionRegistry(language);
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
