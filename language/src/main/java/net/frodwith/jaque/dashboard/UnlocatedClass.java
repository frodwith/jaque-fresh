package net.frodwith.jaque.dashboard;

import java.util.Optional;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;

public abstract class UnlocatedClass extends NockClass {
  protected final Battery battery;

  protected UnlocatedClass(Battery battery, Assumption stable) {
    super(battery, stable);
    this.battery = battery;
  }

  @Override
  public final boolean locatedAt(Location location) {
    return false;
  }

  @Override
  public final boolean copyableEdit(Axis written, Cell batteryCell) {
    return battery.copyableEdit(written, batteryCell);
  }

  @Override
  public Optional<CallTarget> getDriver(Axis axis) {
    return Optional.empty();
  }
}
