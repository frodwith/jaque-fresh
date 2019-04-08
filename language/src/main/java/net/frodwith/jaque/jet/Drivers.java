package net.frodwith.jaque.jet;

import java.util.Optional;
import java.util.function.Function;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.AxisMap;

public final class Drivers {
  private final AstContext context;
  private final AxisMap<CallTarget> targets;
  private final Assumption valid;

  public Drivers(AstContext context, Optional<Location> location) {
    this.context = context;
    this.valid   = context.dashboard.getStableAssumption();
    this.targets = location.isPresent()
                 ? context.getDrivers(location.get())
                 : AxisMap.EMPTY;
  }

  public Optional<CallTarget> getDriver(Axis axis) {
    CallTarget target = targets.get(axis);
    return ( null != t ) ? t : Optional.empty();
  }

  public boolean isValid(AstContext context) {
    return valid.isValid() && this.context.compatible(context);
  }
}
