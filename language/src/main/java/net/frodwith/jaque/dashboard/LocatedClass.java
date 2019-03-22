package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.exception.ExitException;

public final class LocatedClass extends NockClass {
  public final Location location;
  private final AxisMap<CallTarget> drivers;

  public LocatedClass(Battery battery, Assumption stable,
                      Location location, AxisMap<CallTarget> drivers) {
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
  public Optional<CallTarget> getDriver(Axis axis) {
    return Optional.ofNullable(drivers.get(axis));
  }
}
