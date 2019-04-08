package net.frodwith.jaque;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

/* 1) Truffle really wants the TruffleLanguage to "own" any particular AST
 *
 * 2) Jet-enabled nock execution works in the context of a dashboard that adds
 *    driver semantics to certain core activations (nock 9). We handle this in
 *    the dashboard package, and the "root object" is the Dashboard class. ASTs
 *    need to cache pieces of the dashboard state in order to resolve call
 *    targets quickly.
 *  
 *  Thus, in order to reuse an ast that we have cached on a cell, its AstContext
 *  must match the AstContext we are running in.
 */

public final class AstContext {
  public final NockLanguage language;
  public final Dashboard dashboard;
  private final Map<Location,AxisMap<CallTarget>> drivers;
  private final Map<Cell,NockFunction> functions;

  public AstContext(NockLanguage language, Dashboard dashboard) {
    this.language = language;
    this.dashboard = dashboard;
    this.drivers = new HashMap<>();
    this.functions = new HashMap<>();
  }

  public NockFunction getFunction(Cell formula) throws ExitException {
    NockFunction f = functions.get(formula);
    if ( null == f ) {
      f = language.getFunctionFactory(formula).apply(this);
      functions.put(formula, f);
    }
    return f;
  }

  public AxisMap<CallTarget> getDrivers(Location location) {
    AxisMap<CallTarget> m = drivers.get(location);
    if ( null == m ) {
      m = dashboard.getDrivers(location, this);
      drivers.put(location, m);
    }
    return m;
  }

  public boolean compatible(AstContext other) {
    return equals(other);
  }

  public boolean equals(AstContext other) {
    return (other.language == language) && (other.dashboard == dashboard);
  }

  public boolean equals(Object o) {
    return (o instanceof AstContext) && equals(((AstContext) o));
  }

  public int hashCode() {
    return Objects.hash(language, dashboard);
  }
}
