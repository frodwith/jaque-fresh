package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Function;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.jet.Drivers;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.exception.ExitException;

public final class LocatedClass extends NockClass {
  public final Location location;
  private Optional<Drivers> drivers;

  public LocatedClass(Battery battery, Assumption stable, Location location) {
    super(battery, stable);
    this.location = location;
    this.drivers = Optional.empty();
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
  public Optional<Location> getLocation() {
    return Optional.of(location);
  }

  private Drivers getDrivers(AstContext context) {
    Drivers d;
    if ( drivers.isPresent() ) {
      d = drivers.get();
      if ( d.isValid(context) ) {
        return d;
      }
    }
    d = new Drivers(context, location);
    drivers = Optional.of(d);
    return d;
  }

  @Override
  public CallTarget 
    getArm(Axis axis, AstContext context, GetArm g)
      throws ExitException {
    Optional<CallTarget> driver = getDrivers(context).getDriver(axis);
    return driver.isPresent()
      ? driver.get()
      : rawArm(context, g);
  }
}
