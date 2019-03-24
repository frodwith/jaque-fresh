package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.hash.HashCode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellGrain;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.SourceMappedNoun;

import net.frodwith.jaque.jet.JetTree;

import net.frodwith.jaque.dashboard.Battery;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.LocatedClass;
import net.frodwith.jaque.dashboard.NockFunction;

import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.nodes.NockRootNode;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.StrongCellGrainKey;
import net.frodwith.jaque.exception.ExitException;

public final class Dashboard {
  public final FormulaParser parser;
  public final boolean hashDiscovery, fastHints;

  private final NockLanguage language;
  private final GrainSilo silo;
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<StrongCellGrainKey,Registration> cold;
  private final Map<HashCode,Registration> hot;
  private final Map<Location,AxisMap<CallTarget>> drivers;
  private final Cache<Cell,NockFunction> functions;
  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, Dashboard.class);

  private Dashboard(NockLanguage language,
                   GrainSilo silo,
                   Map<StrongCellGrainKey,Registration> cold,
                   Map<HashCode,Registration> hot,
                   Map<Location,AxisMap<CallTarget>> drivers,
                   int functionCacheSize,
                   boolean hashDiscovery,
                   boolean fastHints) {
    this.hot     = hot;
    this.cold    = cold;
    this.drivers = drivers;
    this.silo    = silo;
    this.hashDiscovery = hashDiscovery;
    this.fastHints = fastHints;
    this.language = language;
    this.parser = new FormulaParser(language, this);
    this.functions = CacheBuilder.newBuilder()
      .maximumSize(functionCacheSize).build();
  }

  public static class Builder {
    private NockLanguage language;
    private GrainSilo silo;
    private Map<Cell, Registration> coldHistory;
    private JetTree jetTree;
    private int functionCacheSize = 0;
    private boolean hashDiscovery = false;
    private boolean fastHints = false;

    public Dashboard build() {
      assert( language != null );
      if ( null == silo ) {
        silo = new GrainSilo();
      }

      Map<Location,AxisMap<CallTarget>> drivers = new HashMap<>();
      Map<HashCode,Registration> hot = new HashMap<>();
      Map<StrongCellGrainKey,Registration> cold = new HashMap<>();
      if ( null != coldHistory ) {
        for ( Map.Entry<Cell,Registration> e : coldHistory.entrySet() ) {
          Cell grain = silo.getCellGrain(e.getKey());
          cold.put(new StrongCellGrainKey(grain), e.getValue());
        }
      }

      if ( 0 == functionCacheSize ) {
        functionCacheSize = 1024;
      }

      Dashboard dashboard = new Dashboard(language, silo, cold, 
          hot, drivers, functionCacheSize, hashDiscovery, fastHints);

      if ( null != jetTree ) {
        jetTree.addToMaps(language, dashboard, hot, drivers);
      }
      return dashboard;
    }

    public Builder setJetTree(JetTree jetTree) {
      this.jetTree = jetTree;
      return this;
    }

    public Builder setLanguage(NockLanguage language) {
      this.language = language;
      return this;
    }

    public Builder setSilo(GrainSilo silo) {
      this.silo = silo;
      return this;
    }

    public Builder setColdHistory(Map<Cell,Registration> coldHistory) {
      this.coldHistory = coldHistory;
      return this;
    }

    public Builder setFunctionCacheSize(int functionCacheSize) {
      this.functionCacheSize = functionCacheSize;
      return this;
    }

    public Builder setHashDiscovery(boolean hashDiscovery) {
      this.hashDiscovery = hashDiscovery;
      return this;
    }

    public Builder setFastHints(boolean fastHints) {
      this.fastHints = fastHints;
      return this;
    }
  }

  public AxisMap<CallTarget> getDrivers(Location loc) {
    AxisMap<CallTarget> drive = drivers.get(loc);
    return (null == drive) ? AxisMap.EMPTY : drive;
  }

  private Cell canonicalizeBattery(Cell core) throws ExitException {
    return silo.getCellGrain(Cell.require(core.head));
  }

  // these class objects could in principle be cached in some way, but they are
  // not expensive and the sharing they enable is primarily useful for edit
  public NockClass getClass(Cell core) throws ExitException {
    Cell battery = canonicalizeBattery(core);
    return battery.getMeta()
      .getGrain()
      .getBattery(this, battery)
      .getClass(core, battery);
  }

  public LocatedClass locatedClass(Cell battery, Location location) {
    return new LocatedClass(
      battery.getMeta().getGrain().getBattery(this, battery),
      stable.getAssumption(), location, getDrivers(location));
  }

  public NockFunction compileFormula(Cell formula) throws ExitException {
    NockFunction f = functions.getIfPresent(formula);
    if ( null == f ) {
      Supplier<SourceMappedNoun> sup = () -> {
        try {
          return SourceMappedNoun.fromCell(formula);
        }
        catch ( ExitException e ) {
          throw new RuntimeException("NockFunction.fromCell:supplier", e);
        }
      };
      NockRootNode nockRoot = new NockRootNode(language,
          NockLanguage.DESCRIPTOR, sup, parser.parse(formula));
      RootCallTarget t = Truffle.getRuntime().createCallTarget(nockRoot);
      f = new NockFunction(t, this);
      functions.put(formula, f);
    }
    return f;
  }

  public Optional<Registration> findHot(CellGrain grain, Cell cell) {
    return hashDiscovery
      ? Optional.ofNullable(hot.get(grain.getStrongHash(cell)))
      : Optional.empty();
  }

  public Battery createBattery(Cell cell) {
    Optional<Optional<Registration>> knownUnknowns = hashDiscovery
      ? Optional.empty()
      : Optional.of(Optional.empty());

    Optional<Registration> r = 
      Optional.ofNullable(cold.get(new StrongCellGrainKey(cell)));

    return new Battery(this, r, knownUnknowns);
  }

  public Registration createCold(Cell battery) {
    Registration r = new Registration();
    cold.put(new StrongCellGrainKey(battery), r);
    return r;
  }

  private Registration getCold(Cell core) throws ExitException {
    // call through meta so caching sticks to the cell
    Cell battery = canonicalizeBattery(core);
    return battery.getMeta()
      .getGrain()
      .getBattery(this, battery)
      .getCold(battery);
  }

  private void invalidate() {
    stable.invalidate();
  }

  // unconditional (will not short-circuit)
  public void register(Cell core, FastClue clue) throws ExitException {
    Location loc;
    if ( clue.toParent.isCrash() ) {
      RootLocation root = new RootLocation(clue.name, clue.hooks, core.tail);
      getCold(core).registerRoot(core.tail, root);
      loc = root;
    }
    else {
      Cell parentCore = Cell.require(clue.toParent.fragment(core));
      NockClass parentClass = parentCore.getMeta()
        .getNockClass(parentCore, this);
      if ( !(parentClass instanceof LocatedClass) ) {
        LOG.warning("trying to register " + clue.name +
            " with unlocated parent.");
        return;
      }
      Location parent = ((LocatedClass) parentClass).location;
      Location child = 
        ( clue.toParent == Axis.TAIL && parent instanceof StaticLocation )
        ? new StaticChildLocation(clue.name, clue.hooks, 
            (StaticLocation) parent)
        : new DynamicChildLocation(clue.name, clue.hooks, parent, clue.toParent);
      getCold(core).registerChild(clue.toParent, child, parent);
      loc = child;
    }
    loc.audit(clue);
    invalidate();

    Cell battery = canonicalizeBattery(core);
    Battery b = battery.getMeta().getGrain().getBattery(this, battery);
    Assumption a = getStableAssumption();
    core.getMeta().setClass(new LocatedClass(b, a, loc, getDrivers(loc)));
  }

  public Assumption getStableAssumption() {
    return stable.getAssumption();
  }
}
