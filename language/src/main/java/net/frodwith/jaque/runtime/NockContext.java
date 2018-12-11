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

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.NockOptions;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;

import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.ColdRegistration;


public final class NockContext {
  private final Env env;
  private final NockLanguage language;
  public final Dashboard dashboard;
  public final NockFunctionRegistry functions;

  public NockContext(NockLanguage language, Env env) {
    OptionValues values = env.getOptions();

    JetTree tree =
      language.getJetTree(values.get(NockOptions.JET_TREE));
    Map<Cell,ColdRegistration> cold =
      language.findHistory(values.get(NockOptions.COLD_HISTORY));

    Map<BatteryHash,Registration> hot = new HashMap<>();
    Map<Location,AxisMap<NockFunction>> drivers = new HashMap<>();
    tree.addToMaps(language, hot, drivers);

    this.env       = env;
    this.language  = language;
    this.functions = new NockFunctionRegistry(language);
    this.dashboard = new Dashboard(cold, hot, drivers);
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
