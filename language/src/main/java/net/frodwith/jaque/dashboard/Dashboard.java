package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Function;

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
import net.frodwith.jaque.AstContext;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellGrain;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;

import net.frodwith.jaque.jet.JetTree;

import net.frodwith.jaque.dashboard.Battery;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.LocatedClass;

import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.nodes.NockRootNode;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.StrongCellGrainKey;
import net.frodwith.jaque.exception.ExitException;

public final class Dashboard {
  public final boolean hashDiscovery, fastHints;

  private final GrainSilo silo;
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<StrongCellGrainKey,Registration> cold;
  private final Map<HashCode,Registration> hot;
  private final Map<Location,AxisMap<Function<AstContext,CallTarget>>> drivers;
  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, Dashboard.class);

  private Dashboard(GrainSilo silo,
                    Map<StrongCellGrainKey,Registration> cold,
                    Map<HashCode,Registration> hot,
                    Map<Location,AxisMap<Function<AstContext,CallTarget>>> drivers,
                    boolean hashDiscovery,
                    boolean fastHints) {
    this.hot     = hot;
    this.cold    = cold;
    this.drivers = drivers;
    this.silo    = silo;
    this.hashDiscovery = hashDiscovery;
    this.fastHints = fastHints;
  }

  public static class Builder {
    private GrainSilo silo;
    private Map<Cell, Registration> coldHistory;
    private JetTree jetTree;
    private boolean hashDiscovery = false;
    private boolean fastHints = false;

    public Dashboard build() {
      if ( null == silo ) {
        silo = new GrainSilo();
      }

      Map<Location,AxisMap<Function<AstContext,CallTarget>>> drivers = new HashMap<>();
      Map<HashCode,Registration> hot = new HashMap<>();
      Map<StrongCellGrainKey,Registration> cold = new HashMap<>();
      if ( null != coldHistory ) {
        for ( Map.Entry<Cell,Registration> e : coldHistory.entrySet() ) {
          Cell grain = silo.getCellGrain(e.getKey());
          cold.put(new StrongCellGrainKey(grain), e.getValue());
        }
      }

      if ( null == jetTree ) {
        hashDiscovery = false;
      }

      Dashboard dashboard = new Dashboard(silo, cold, 
          hot, drivers, hashDiscovery, fastHints);

      if ( null != jetTree ) {
        jetTree.addToMaps(hot, drivers);
      }
      return dashboard;
    }

    public Builder setJetTree(JetTree jetTree) {
      this.jetTree = jetTree;
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

    public Builder setHashDiscovery(boolean hashDiscovery) {
      this.hashDiscovery = hashDiscovery;
      return this;
    }

    public Builder setFastHints(boolean fastHints) {
      this.fastHints = fastHints;
      return this;
    }
  }
  
  public AxisMap<CallTarget> getDrivers(Location location, AstContext context) {
    return drivers.containsKey(location)
      ? drivers.get(location).transform((f) -> f.apply(context))
      : AxisMap.EMPTY;
  }

  private Cell canonicalizeBattery(Cell core) throws ExitException {
    return silo.getCellGrain(Cell.require(core.head));
  }

  public NockClass getNockClass(Cell core) throws ExitException {
    Cell battery = canonicalizeBattery(core);
    return battery.getMeta()
      .getGrain()
      .getBattery(this, battery)
      .getNockClass(core, battery);
  }

  public LocatedClass locatedClass(Cell battery, Location location) {
    return new LocatedClass(
      battery.getMeta().getGrain().getBattery(this, battery),
      stable.getAssumption(), location);
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
  public void 
    register(Cell core, FastClue clue)
      throws ExitException {
    Location location;
    if ( clue.toParent.isCrash() ) {
      RootLocation root = new RootLocation(clue.name, clue.hooks, core.tail);
      getCold(core).registerRoot(core.tail, root);
      location = root;
    }
    else {
      Cell parentCore = Cell.require(clue.toParent.fragment(core));
      NockClass nc = parentCore.getMeta().getNockClass(core, this);
      Optional<Location> parentLocation = nc.getLocation();

      if ( !parentLocation.isPresent() ) {
        LOG.warning("trying to register " + clue.name +
                    " with unlocated parent. NockClass = " + nc.toString());
        return;
      }
      Location parent = parentLocation.get();
      Location child = 
        ( clue.toParent.isTail() && parent instanceof StaticLocation )
        ? new StaticChildLocation(clue.name, clue.hooks, 
            (StaticLocation) parent)
        : new DynamicChildLocation(clue.name, clue.hooks, parent, clue.toParent);
      getCold(core).registerChild(clue.toParent, child, parent);
      location = child;
    }
    location.audit(clue);
    invalidate();

    if (clue.name.equals("fond")) {
      //      new Exception().printStackTrace();
      System.err.println("Clue name '" + clue.name + "' at " + location);
    }

    Cell battery = canonicalizeBattery(core);
    Battery b = battery.getMeta().getGrain().getBattery(this, battery);
    core.getMeta()
      .setNockClass(new LocatedClass(b, getStableAssumption(), location));
  }

  public Assumption getStableAssumption() {
    return stable.getAssumption();
  }
}
