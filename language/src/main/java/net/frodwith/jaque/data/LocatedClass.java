package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.exception.ExitException;

public final class LocatedClass extends NockClass {
  public final Location location;
  public final AxisMap<NockFunction> drivers;

  public LocatedClass(Battery battery, Assumption stable,
                      Location location, AxisMap<NockFunction> drivers) {
    super(battery, stable);
    this.location = location;
    this.drivers = drivers;
  }

  @Override
  public final NockFunction 
    getArm(Axis axis, FragmentNode fragment,
           Supplier<NockFunctionRegistry> supply)
      throws ExitException {
    NockFunction f = drivers.get(axis);
    return ( null != f ) ? f : battery.getArm(fragment, supply);
  }
  
  @Override
  public final FineCheck
    getFine(Cell core, Supplier<Dashboard> supply)
      throws ExitException {
    return location.buildFine(core, supply);
  }

  @Override
  public final boolean copyableEdit(Axis written) {
    return location.copyableEdit(written);
  }
}
