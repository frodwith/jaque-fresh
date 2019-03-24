package net.frodwith.jaque.runtime;

import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionType;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import com.google.common.hash.HashCode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.NockOptions;
import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockFunction;

public final class NockContext {
  public final Dashboard dashboard;

  private final Env env;
  private final NockLanguage language;
  private final Cache<Cell,Object> memoCache;

  public NockContext(NockLanguage language, Env env) {
    OptionValues values = env.getOptions();

    JetTree tree =
      language.getJetTree(values.get(NockOptions.JET_TREE));

    Map<Cell,Registration> coldHistory =
      language.findHistory(values.get(NockOptions.COLD_HISTORY));

    this.env       = env;
    this.language  = language;

    this.dashboard = new Dashboard.Builder()
      .setLanguage(language)
      .setColdHistory(coldHistory)
      .setJetTree(tree)
      .setHashDiscovery(values.get(NockOptions.HASH))
      .setFastHints(values.get(NockOptions.FAST))
      .build();

    this.memoCache = CacheBuilder.newBuilder()
      .maximumSize(values.get(NockOptions.MEMO_SIZE))
      .build();
  }

  public Object lookupMemo(Object subject, Cell formula) {
    return memoCache.getIfPresent(new Cell(subject, formula));
  }

  public void recordMemo(Object subject, Cell formula, Object product) {
    memoCache.put(new Cell(subject, formula), product);
  }
}
