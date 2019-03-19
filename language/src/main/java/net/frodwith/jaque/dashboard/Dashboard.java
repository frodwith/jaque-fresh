package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.concurrent.ExecutionException;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellGrain;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockClass;
import net.frodwith.jaque.data.RegisteredClass;
import net.frodwith.jaque.data.UnregisteredClass;
import net.frodwith.jaque.data.LocatedClass;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.Battery;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.nodes.NockRootNode;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.StrongCellGrainKey;
import net.frodwith.jaque.exception.ExitException;

public final class Dashboard {
  public final NockContext context;
  public final FormulaParser parser;
  private final NockLanguage language;
  private final GrainSilo silo;
  private final boolean hashDiscovery;
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<StrongCellGrainKey,Registration> cold;
  private final Map<HashCode,Registration> hot;
  private final Map<Location,AxisMap<NockFunction>> drivers;
  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, Dashboard.class);

  public Dashboard(NockContext context,
                   NockLanguage language,
                   GrainSilo silo,
                   Map<StrongCellGrainKey,Registration> cold,
                   Map<HashCode,Registration> hot,
                   Map<Location,AxisMap<NockFunction>> drivers,
                   boolean hashDiscovery) {
    this.hot     = hot;
    this.cold    = cold;
    this.drivers = drivers;
    this.context = context;
    this.silo    = silo;
    this.hashDiscovery = hashDiscovery;
    this.language = language;
    this.parser = new FormulaParser(language, this);
  }

  public AxisMap getDrivers(Location loc) {
    AxisMap<NockFunction> drive = drivers.get(loc);
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

  public NockFunction compileFormula(Cell formula) throws ExitException {
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
    return new NockFunction(t, this);
  }

  public NockObject getObject(Cell core) throws ExitException {
    return new NockObject(getClass(core), core);
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
    Registration r = new Registration(context);
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
      NockClass parentClass = parentCore.getMeta().getObject(context, parentCore).klass;
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

    Assumption a = getStableAssumption();
    Cell battery = canonicalizeBattery(core);
    Battery    b = battery.getMeta().getGrain().getBattery(this, battery);
    LocatedClass klass = new LocatedClass(b, a, loc, getDrivers(loc));
    NockObject object  = new NockObject(klass, core);
    core.getMeta().setObject(object);
  }

  public Assumption getStableAssumption() {
    return stable.getAssumption();
  }
}
