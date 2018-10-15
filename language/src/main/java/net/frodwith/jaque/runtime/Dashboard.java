package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Location;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockBattery;

public final class Dashboard {
  private CyclicAssumption stable = new CyclicAssumption("dashboard");
  private Map<Cell,NockBattery> batteries;

  public Dashboard() {
    this.batteries = new HashMap<>();
  }

  public Location locate(Cell core) {
    // XX TODO
    return null;
  }

  public NockObject getObject(Cell core) {
    Location location = locate(core);
    return new NockObject(core, stable.getAssumption(), location);
  }

  @TruffleBoundary
  public NockBattery getBattery(Cell battery) {
    NockBattery meta = batteries.get(battery);
    if ( null == meta ) {
      meta = new NockBattery();
      batteries.put(battery, meta);
    }
    return meta; }
}
