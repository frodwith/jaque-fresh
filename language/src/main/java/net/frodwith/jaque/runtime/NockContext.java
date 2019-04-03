package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionType;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;

public final class NockContext {
  private final Env env;
  private final Cache<Cell,Object> memoCache;
  private final AstContext astContext;

  public NockContext(Env env, AstContext astContext) {
    this.env  = env;
    this.astContext = astContext;

    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(env.getOptions().get(NockOptions.MEMO_SIZE))
      .build();
  }

  public AstContext getAstContext() {
    return astContext;
  }

  public Object lookupMemo(Object subject, Cell formula) {
    return memoCache.getIfPresent(new Cell(subject, formula));
  }

  public void recordMemo(Object subject, Cell formula, Object product) {
    memoCache.put(new Cell(subject, formula), product);
  }
}
