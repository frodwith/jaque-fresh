package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.function.Supplier;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionType;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.UnsupportedTypeException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.interop.Bindings;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;

public final class NockContext {
  private final Env env;
  private final Cache<Object,Object> memoCache;
  private final ArrayDeque<Object> flyStack;
  private AstContext astContext;
  private static final String dashboardRequired = Dashboard.class + " required";

  public NockContext(Env env, AstContext astContext) {
    this.astContext = astContext;
    this.env  = env;

    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(env.getOptions().get(NockOptions.MEMO_SIZE))
      .build();

    if ( env.isPolyglotAccessAllowed() ) {
      env.exportSymbol("nock", new Bindings(this));
    }

    flyStack = new ArrayDeque<>();
  }

  public AstContext getAstContext() {
    return astContext;
  }

  public void setDashboard(Dashboard dashboard) {
    astContext = astContext.language.getAstContext(dashboard);
  }

  public Dashboard asDashboard(Object value)
    throws UnsupportedTypeException {
    if ( env.isHostObject(value) ) {
      Object hostObject = env.asHostObject(value);
      if ( hostObject instanceof Dashboard ) {
        return (Dashboard) hostObject;
      }
    }
    throw UnsupportedTypeException.create(new Object[] { value} , dashboardRequired);
  }

  public Dashboard getDashboard() {
    return astContext.dashboard;
  }

  // the keys are objects which must override .equals() and .hashCode()
  @TruffleBoundary
  public Object lookupMemo(Object key) {
    return memoCache.getIfPresent(key);
  }

  @TruffleBoundary
  public void recordMemo(Object key, Object product) {
    memoCache.put(key, product);
  }

  public Object asHostObject(Object polyHostObject) {
    return env.asHostObject(polyHostObject);
  }

  public int flyCount() {
    return flyStack.size();
  }

  public <T> T withFly(Object flyGate, Supplier<T> thunk) {
    try {
      flyStack.push(flyGate);
      return thunk.get();
    }
    finally {
      flyStack.pop();
    }
  }

  public <T> T peelFly(Function<Object,T> withPeeled) {
    if ( flyStack.isEmpty() ) {
      return withPeeled.apply(null);
    }
    else {
      Object fly = flyStack.pop();
      try {
        return withPeeled.apply(fly);
      }
      finally {
        flyStack.push(fly);
      }
    }
  }

  public Iterable<Object> flyGates() {
    return flyStack;
  }
}
