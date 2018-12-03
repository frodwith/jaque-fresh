package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.hash.Hashing;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockClass;
import net.frodwith.jaque.data.RegisteredClass;
import net.frodwith.jaque.data.UnregisteredClass;
import net.frodwith.jaque.data.LocatedClass;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.Battery;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.exception.ExitException;

public final class Dashboard {
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<Cell,ColdRegistration> cold;
  private final Map<BatteryHash,Registration> hot;
  private final Map<Location,AxisMap<NockFunction>> drivers;
  private final Cache<Cell,Battery> batteries;

  public Dashboard(Map<Cell,ColdRegistration> cold,
                   Map<BatteryHash,Registration> hot,
                   Map<Location,AxisMap<NockFunction>> drivers) {
    this.hot       = hot;
    this.cold      = cold;
    this.drivers   = drivers;
    this.batteries = CacheBuilder.newBuilder().softValues().build();
  }

  // these class objects could in principle be cached in some way, but they are
  // not expensive and the sharing they enable is primarily useful for edit
  public NockClass getClass(Cell core) throws ExitException {
    final Battery   b = getBattery(Cell.require(core.head));
    final Dashboard d = this;
    Location      loc = null;

    if ( null != b.cold ) {
      loc = b.cold.locate(core, () -> d);
    }

    if ( null == loc ) {
      if ( null != b.hot ) {
        if ( null != (loc = b.hot.locate(core, () -> d)) ) {
          loc.register(freeze(b));
          stable.invalidate();
        }
      }
    }

    final Assumption a = stable.getAssumption();

    if ( null != loc ) {
      AxisMap<NockFunction> drive = drivers.get(loc);
      if ( drive == null ) {
        drive = AxisMap.EMPTY;
      }
      return new LocatedClass(b, a, loc, drive);
    }
    else if ( (null == b.cold) && (null == b.hot) ) {
      return new UnregisteredClass(b, a);
    }
    else {
      return new RegisteredClass(b, a);
    }
  }

  public NockObject getObject(Cell core) throws ExitException {
    return new NockObject(getClass(core), core);
  }

  @TruffleBoundary
  public Battery getBattery(Cell battery) {
    try {
      return batteries.get(battery, () -> makeBattery(battery));
    }
    catch ( ExecutionException e ) {
      throw new AssertionError();
    }
  }

  private Battery makeBattery(Cell noun) {
    BatteryHash hash;
    Registration r;
    ColdRegistration cr = cold.get(noun);
    if ( null == cr ) {
      hash = BatteryHash.hash(noun);
      r    = null;
    }
    else {
      hash = cr.hash;
      r    = cr.registration;
    }
    return new Battery(noun, hash, r, hot.get(hash));
  }

  private Registration freeze(Battery battery) {
    if ( null == battery.cold ) {
      ColdRegistration cr = new ColdRegistration(battery.hash);
      battery.cold = cr.registration;
      cold.put(battery.noun, cr);
    }
    return battery.cold;
  }

  private Registration getRegistration(Cell core) throws ExitException {
    // call through meta so caching sticks to the cell
    Dashboard supply = this;
    return freeze(Cell.require(core.head).getMeta().getBattery(() -> supply));
  }

  // unconditional (will not short-circuit)
  public void register(Cell core, FastClue clue) throws ExitException {
    if ( clue.toParent.isCrash() ) {
      RootLocation root = new RootLocation(clue.name, clue.hooks, core.tail);
      getRegistration(core).registerRoot(core.tail, root);
    }
    else {
      Cell parentCore = Cell.require(clue.toParent.fragment(core));
      Dashboard supply = this;
      NockClass parentClass = parentCore.getMeta()
        .getObject(() -> supply).klass;
      if ( !(parentClass instanceof LocatedClass) ) {
        // XX log the fact we tried to register a core with an unlocated parent
        return;
      }
      Location parent = ((LocatedClass) parentClass).location;
      Location child = 
        ( clue.toParent == Axis.TAIL && parent instanceof StaticLocation )
        ? new StaticChildLocation(clue.name, clue.hooks, 
            (StaticLocation) parent)
        : new DynamicChildLocation(clue.name, clue.hooks, parent, clue.toParent);
      getRegistration(core).registerChild(clue.toParent, child, parent);
    }
    stable.invalidate();
  }
}
