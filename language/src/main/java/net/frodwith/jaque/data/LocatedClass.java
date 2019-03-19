package net.frodwith.jaque.data;

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

  @Override
  public final NockFunction
    getArm(Axis axis, FragmentNode fragment, 
           Cell batteryCell, Dashboard dashboard)
      throws ExitException {
    NockFunction f = drivers.get(axis);
    return ( null != f ) ? f : battery.getArm(fragment, batteryCell, dashboard);
  }

  @Override
  public final NockFunction
    getArm(Axis axis, Cell batteryCell, Dashboard dashboard) throws ExitException {
    NockFunction f = drivers.get(axis);
    return ( null != f ) ? f : battery.getArm(axis.mas(), batteryCell, dashboard);
  }
  
  @Override
  public final FineCheck getFine(Cell core, NockContext context)
    throws ExitException {
    return location.buildFine(core, context);
  }

  @Override
  public final boolean copyableEdit(Cell battery, Axis written) {
    return location.copyableEdit(written);
  }

  @Override
  public final boolean locatedAt(Location location) {
    return this.location.equals(location);
  }
}
