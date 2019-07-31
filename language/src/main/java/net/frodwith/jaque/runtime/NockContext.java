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
import com.oracle.truffle.api.interop.UnsupportedTypeException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.util.NounMap;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.ConstantCell;
import net.frodwith.jaque.data.ConstantAtom;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.interop.Bindings;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.library.NounLibrary;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;

public final class NockContext {
  private final Env env;
  private final Cache<Cell,Object> memoCache;
  private AstContext astContext; // DEPRECATED
  private static final String dashboardRequired = Dashboard.class + " required";
  private final NounMap<ConstantCell> cellPool;
  private final NounMap<ConstantAtom> atomPool;
  private static final NounLibrary nouns = NounLibrary.getUncached();

  public NockContext(Env env, AstContext astContext) {
    this.env  = env;

    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(env.getOptions().get(NockOptions.MEMO_SIZE))
      .build();

    this.cellPool = new NounMap();
    this.atomPool = new NounMap();

    if ( env.isPolyglotAccessAllowed() ) {
      env.exportSymbol("nock", new Bindings(this));
    }

    // So deprecated. Ugh.
    this.astContext = astContext;
  }

  // intern methods are slow-path
  public Object internNoun(Object noun) throws ExitException {
    CompilerAsserts.neverPartOfCompilation();
    return nouns.isCell(noun) ? internCell(noun) : internAtom(noun);
  }

  public ConstantCell internCell(Object cell) throws ExitException {
    CompilerAsserts.neverPartOfCompilation();
    ConstantCell c = nouns.knownConstantCell(cell);
    if ( null == c ) {
      c = cellPool.get(cell);
      if ( null == c ) {
        c = new ConstantCell(this, 
          internNoun(nouns.head(cell)),
          internNoun(nouns.tail(cell)),
          nouns.mug(cell));
        cellPool.put(c, c);
      }
      nouns.learnConstantCell(cell, c);
    }
    return c;
  }

  public Object internAtom(Object atom) throws ExitException {
    CompilerAsserts.neverPartOfCompilation();
    if ( nouns.fitsInLong(atom) ) {
      return nouns.asLong(atom);
    }
    else {
      ConstantAtom a = atomPool.get(atom);
      if ( null == a ) {
        atomPool.put(atom, a = new ConstantAtom(nouns.asIntArray(atom)));
      }
      return a;
    }
  }

  public boolean isHostObject(Object object) {
    return env.isHostObject(object);
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

  public Object lookupMemo(Object subject, Cell formula) {
    return memoCache.getIfPresent(new Cell(subject, formula));
  }

  public void recordMemo(Object subject, Cell formula, Object product) {
    memoCache.put(new Cell(subject, formula), product);
  }
}
