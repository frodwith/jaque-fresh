package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.Location;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockBattery;

public final class Dashboard {
  private final CyclicAssumption stable = new CyclicAssumption("dashboard");
  private final Map<Cell,NockBattery> batteries;
  private final Map<Location,AxisMap<NockFunction>> drivers;

  public Dashboard(Map<Location,AxisMap<NockFunction>> drivers) {
    this.batteries = new HashMap<>();
    this.drivers = drivers;
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
  public NockBattery getBattery(Cell battery) {
    return batteries.get(battery);
  }

  public void register(Cell core, FastClue clue) {
    // XX todo
  }
}
