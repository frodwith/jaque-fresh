package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;

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
  public Optional<NockFunction> getDriver(Axis axis) {
    return Optional.empty();
  }
}
