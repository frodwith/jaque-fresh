package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class LocatedClass extends NockClass {
  public final Location location;
  private final AxisMap<NockFunction> drivers;

  public LocatedClass(Battery battery, Assumption stable,
                      Location location, AxisMap<NockFunction> drivers) {
    super(battery, stable);
    this.location = location;
    this.drivers = drivers;
  }

  public boolean known(Cell core) {
    return core.knownAt(location, battery.dashboard);
  }

  @Override
  protected FineCheck buildFine(Cell core) throws ExitException {
    return location.buildFine(core, battery.dashboard);
  }

  @Override
  public boolean copyableEdit(Axis written, Cell battery) {
    return location.copyableEdit(written);
  }

  @Override
  public boolean locatedAt(Location location) {
    return this.location.equals(location);
  }

  @Override
  public Optional<NockFunction> getDriver(Axis axis) {
    return Optional.ofNullable(drivers.get(axis));
  }
}
