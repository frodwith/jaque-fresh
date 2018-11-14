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
  private final Map<Cell,Registration> registry;
  private final Map<Location,AxisMap<NockFunction>> drivers;
  private final Cache<Cell,Battery> batteries;

  public Dashboard(Map<Cell,Registration> registry,
                   Map<Location,AxisMap<NockFunction>> drivers) {
    this.drivers = drivers;
    this.registry = registry;
    this.batteries = CacheBuilder.newBuilder().softValues().build();
  }

  // these class objects could in principle be cached in some way, but they are
  // not expensive and the sharing they enable is primarily useful for edit
  public NockClass getClass(Cell core) throws ExitException {
    final Assumption   a = stable.getAssumption();
    final Battery      b = getBattery(Cell.require(core.head));
    final Registration r = b.registration;

    if ( null == r ) {
      return new UnregisteredClass(b, a);
    }
    else {
      final Dashboard  d = this;
      final Location loc = r.locate(core, () -> d);
      if ( null == loc ) {
        return new RegisteredClass(b, a, r);
      }
      else {
        AxisMap<NockFunction> drive = drivers.get(loc);
        if ( drive == null ) {
          drive = AxisMap.EMPTY;
        }
        return new LocatedClass(b, a, loc, drive);
      }
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

  private Battery makeBattery(Cell battery) {
    byte[] sha = new byte[256];
    // TODO: compute real sha hash
    for ( int i = 0; i < 256; ++i ) {
      sha[i] = (byte) i;
    }
    return new Battery(sha, battery, registry.get(battery));
  }

  private Registration getRegistration(Cell core) throws ExitException {
    // call through meta so caching sticks to the cell
    Dashboard supply = this;
    Battery battery = Cell.require(core.head)
      .getMeta().getBattery(() -> supply);
    return null == battery.registration
      ? (battery.registration = new Registration(battery.hash))
      : battery.registration;
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
