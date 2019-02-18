package net.frodwith.jaque.runtime;

import java.io.IOException;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionType;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.HoonLanguage;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockControlFlowException;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.ColdRegistration;

public final class NockContext {
  private final Env env;
  private final NockLanguage language;
  private final Map<Cell,NockFunction> functions;
  private final Cache<Cell,Object> memoCache;

  public final FormulaParser parser;
  public final Dashboard dashboard;
  public final boolean fast, hash;

  public NockContext(NockLanguage language, Env env, 
    FormulaParser parser,
    int memoSize, boolean fast, boolean hash,
    JetTree jets, Map<Cell,ColdRegistration> cold) {

    Map<BatteryHash,Registration> hot = new HashMap<>();
    Map<Location,AxisMap<NockFunction>> drivers = new HashMap<>();
    jets.addToMaps(language, this, hot, drivers);

    this.env       = env;
    this.language  = language;
    this.fast      = fast;
    this.hash      = hash;
    this.parser    = parser;
    this.functions = new HashMap<>();
    this.dashboard = new Dashboard(this, cold, hot, drivers);
    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(memoSize)
      .build();
  }

  public CellMeta cellMeta(Cell cell) {
    return new CellMeta(dashboard, parser, cell);
  }

  public NockFunction lookupFunction(Cell formula) throws ExitException {
    NockFunction f = functions.get(formula);
    if ( null == f ) {
      f = new NockFunction(parser.cellTarget(formula));
      functions.put(formula, f);
    }
    return f;
  }

  public Object lookupMemo(Object subject, Cell formula) {
    return memoCache.getIfPresent(new Cell(subject, formula));
  }

  public void recordMemo(Object subject, Cell formula, Object product) {
    memoCache.put(new Cell(subject, formula), product);
  }


  /* "nodeless" dispatch... maybe a bad idea. */
  public static Object dispatch(NockFunction function, Object subject) {
    while ( true ) {
      try {
        return function.callTarget.call(subject);
      }
      catch ( NockControlFlowException e ) {
        subject = e.call.subject;
        function = e.call.function;
      }
    }
  }

  public Object pullArm(Axis arm, Object core) throws ExitException {
    NockFunction function = Cell.require(core)
      .getMeta(this)
      .getObject()
      .getArm(arm, this);

    return dispatch(function, core);
  }

  public Object slamGate(Object gate, Object... arguments)
    throws ExitException {
    if ( 0 != arguments.length ) {
      gate = Axis.SAMPLE.edit(gate, NockLanguage.fromArguments(arguments));
    }
    return pullArm(Axis.HEAD, gate);
  }

  public Object slamHoon(String hoon, Object... arguments)
    throws ExitException, IOException {
    Source src = Source
      .newBuilder(HoonLanguage.ID, hoon, "<slamHoon>")
      .build();
    return slamGate(env.parse(src).call(), arguments);
  }
}
