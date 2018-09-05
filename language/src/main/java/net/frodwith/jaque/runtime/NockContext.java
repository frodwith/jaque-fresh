package net.frodwith.jaque.runtime;

import com.oracle.truffle.api.TruffleLanguage.Env;

import net.frodwith.jaque.NockLanguage;

public final class NockContext {
  private final Env env;

  public NockContext(NockLanguage language, Env env) {
    this.env = env;
  }
      
}
