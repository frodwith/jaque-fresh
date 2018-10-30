package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;

import com.google.common.hash;

import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.Location;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockBattery;

public final class Dashboard {
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<Cell,Registration> registry;
  private final Map<Location,AxisMap<NockFunction>> drivers;
  private final LoadingCache<Cell,Battery> batteries;

  public Dashboard(ContextReference<NockContext> contextReference,
                   Map<Location,AxisMap<NockFunction>> drivers) {
    this.drivers = drivers;
    this.contextReference = contextReference;
    this.batteries = CacheBuilder.newBuilder().softValues();
  }

  public NockObject getObject(Cell core) {
    Assumption ass = stable.getAssumption();
    try {
      NockBattery battery = getBattery(Cell.require(core.head));
      if ( null != battery ) {
        Location loc = battery.locate(core);
        if ( null != loc ) {
          return new NockObject(core, loc, ass, drivers.get(loc));
        }
      }
    }
    catch ( ExitException e ) {
    }
    return new NockObject(core, null, ass, null);
  }

  @TruffleBoundary
  public Battery getBattery(Cell battery) {
    return batteries.get(battery);
  }

  private Registration getRegistration(Cell core) throws ExitException {
    // call through meta so caching sticks to the cell
    Battery battery = Cell.require(core.head).getMeta().getBattery(this);
    if ( null == battery.registration ) {
      battery.registration = new Registration(battery.hash);
    }
    return battery.registration;
  }

  // unconditional (will not short-circuit)
  public void register(Cell core, FastClue clue) throws ExitException {
    if ( clue.toParent.isCrash() ) {
      RootLocation root = new RootLocation(clue.name, clue.hooks, core.tail);
      getRegistration(core).registerRoot(core.tail, root);
    }
    else {
      Cell parentCore = Cell.require(clue.toParent.fragment(core));
      Location parent = parentCore.getObject().location;
      if ( null == parent ) {
        // XX log the fact we tried to register a core with an unlocated parent
        return;
      }
      Location child = 
        ( clue.toParent == Axis.TAIL && parent instanceof StaticLocation )
        ? new StaticChildLocation(clue.name, clue.hooks, 
            (StaticLocation) parent)
        ? new DynamicChildLocation(clue.name, clue.hooks, parent, toParent);
      getRegistration(core).registerChild(toParent, child, parent);
    }
    stable.invalidate();
  }
}
