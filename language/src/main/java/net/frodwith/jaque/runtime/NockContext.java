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

import com.google.common.hash.HashCode;
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

public final class NockContext {
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

    Map<Cell,Registration> coldHistory =
      language.findHistory(values.get(NockOptions.COLD_HISTORY));

    GrainSilo silo = new GrainSilo(); // FIXME: get from env, but also dashboard

    Map<StrongCellGrainKey,Registration> cold = new HashMap<>();
    for ( Map.Entry<Cell,Registration> e : coldHistory.entrySet() ) {
      Cell grain = silo.getCellGrain(e.getKey());
      cold.put(new StrongCellGrainKey(grain), e.getValue());
    }

    Map<HashCode,Registration> hot = new HashMap<>();
    Map<Location,AxisMap<NockFunction>> drivers = new HashMap<>();

    this.env       = env;
    this.language  = language;
    this.fast      = values.get(NockOptions.FAST);
    this.hash      = values.get(NockOptions.HASH);
    this.dashboard = new Dashboard(this, language, silo, cold, hot, drivers, hash);
    this.parser    = new FormulaParser(language, dashboard);
    this.functions = new HashMap<>();
    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(values.get(NockOptions.MEMO_SIZE))
      .build();

    tree.addToMaps(language, dashboard, this, hot, drivers);
  }

  public Object lookupMemo(Object subject, Cell formula) {
    return memoCache.getIfPresent(new Cell(subject, formula));
  }

  public void recordMemo(Object subject, Cell formula, Object product) {
    memoCache.put(new Cell(subject, formula), product);
  }
}
