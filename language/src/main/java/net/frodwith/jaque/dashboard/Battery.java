package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

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
  
  public NockClass getNockClass(Cell core, Cell battery) {
    Optional<Location> location = locate(core, battery);
    Assumption stable = dashboard.getStableAssumption();
    if ( location.isPresent() ) {
      return new LocatedClass(this, stable, location.get());
    }
    else if ( cold.isPresent() ) {
      return new RegisteredClass(this, stable);
    }
    else {
      return new UnregisteredClass(this, stable);
    }
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
