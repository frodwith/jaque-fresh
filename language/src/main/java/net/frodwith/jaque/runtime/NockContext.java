package net.frodwith.jaque.runtime;

import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionType;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.FormulaCompiler;
import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.ColdRegistration;

public final class NockContext implements FormulaCompiler {
  private final Env env;
  private final NockLanguage language;
  private final FormulaParser parser;
  private final Map<Cell,NockFunction> functions;
  private final Cache<Cell,Object> memoCache;
  public final Dashboard dashboard;
  public final boolean fast, hash;

  public NockContext(NockLanguage language, Env env) {
    OptionValues values = env.getOptions();

    JetTree tree =
      language.getJetTree(values.get(NockOptions.JET_TREE));
    Map<Cell,ColdRegistration> cold =
      language.findHistory(values.get(NockOptions.COLD_HISTORY));

    Map<BatteryHash,Registration> hot = new HashMap<>();
    Map<Location,AxisMap<NockFunction>> drivers = new HashMap<>();
    tree.addToMaps(language, this, hot, drivers);

    this.env       = env;
    this.language  = language;
    this.fast      = values.get(NockOptions.FAST);
    this.hash      = values.get(NockOptions.HASH);
    this.parser    = new FormulaParser(language);
    this.functions = new HashMap<>();
    this.dashboard = new Dashboard(this, cold, hot, drivers);
    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(values.get(NockOptions.MEMO_SIZE))
      .build();
  }

  public NockFunction compile(Cell formula) throws ExitException {
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
}
