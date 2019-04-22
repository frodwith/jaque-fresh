package net.frodwith.jaque.data;

import java.util.function.Function;

import com.oracle.truffle.api.RootCallTarget;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.runtime.NockContext;

public final class NockFunction {
  private final AstContext context;
  private final Function<AstContext,RootCallTarget> factory;
  public final RootCallTarget callTarget;

  public NockFunction(AstContext context, 
                      Function<AstContext,RootCallTarget> factory) {
    this.context = context;
    this.factory = factory;
    this.callTarget = factory.apply(context);
  }

  public NockFunction forContext(AstContext context) {
    return new NockFunction(context, factory);
  }

  public boolean compatible(AstContext context) {
    return context.compatible(context);
  }
}
