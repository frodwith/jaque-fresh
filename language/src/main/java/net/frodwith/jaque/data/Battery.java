package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

// these objects live on grains, and so are guaranteed to be deduplicated
// therefore there is no synchronization or assumptions required to ensure that
// cold is up to date even if "another" battery with this noun value is
// registered - there is only one per noun value in a silo.

public final class Battery {
  public final Dashboard dashboard;

  private Optional<Optional<Registration>> hot;
  private Optional<Registration> cold;

  public Battery(Dashboard dashboard,
                 Optional<Registration> cold, 
                 Optional<Optional<Registration>> hot) {
    this.dashboard = dashboard;
    this.hot = hot;
    this.cold = cold;
  }

  private Optional<Registration> getHot(Cell cell) {
    if ( hot.isPresent() ) {
      return hot.get();
    }
    else {
      Optional<Registration> r =
        dashboard.findHot(cell.getMeta().getGrain(), cell);
      hot = Optional.of(r);
      return r;
    }
  }

  public boolean ofDashboard(Dashboard dashboard) {
    return dashboard == this.dashboard;
  }

  public Registration getCold(Cell battery) {
    if ( cold.isPresent() ) {
      return cold.get();
    }
    else {
      Registration r = dashboard.createCold(battery);
      cold = Optional.of(r);
      return r;
    }
  }

  public Optional<Location> locate(Cell core, Cell battery) {
    Optional<Location> location = Optional.empty();
    if ( cold.isPresent() ) {
      location = cold.get().locate(core, dashboard);
    }
    Optional<Registration> hot = getHot(battery);
    if ( !location.isPresent() && hot.isPresent() ) {
      location = hot.get().locate(core, dashboard);
      if ( location.isPresent() ) {
        location.get().register(getCold(battery));
      }
    }
    return location;
  }
  
  public NockClass getClass(Cell core, Cell battery) {
    Optional<Location> location = locate(core, battery);
    Assumption stable = dashboard.getStableAssumption();
    if ( location.isPresent() ) {
      Location l = location.get();
      return new LocatedClass(this, stable, l, dashboard.getDrivers(l));
    }
    else if ( cold.isPresent() ) {
      return new RegisteredClass(this, stable);
    }
    else {
      return new UnregisteredClass(this, stable);
    }
  }

  public NockFunction getArm(Cell battery, FragmentNode fragmentNode)
      throws ExitException {
    Cell formula = Cell.require(fragmentNode.executeFragment(battery));
    return formula.getMeta().getFunction(formula, dashboard);
  }

  public NockFunction getArm(Cell battery, Axis axis) throws ExitException {
    Cell formula = Cell.require(axis.fragment(battery));
    return formula.getMeta().getFunction(formula, dashboard);
  }

  public boolean copyableEdit(Axis written, Cell battery) {
    if ( written.inTail() && 
        ( !cold.isPresent() || cold.get().copyableEdit(written) ) ) {
      Optional<Registration> hot = getHot(battery);
      return !hot.isPresent() || hot.get().copyableEdit(written);
    }
    else {
      return false;
    }
  }
}
