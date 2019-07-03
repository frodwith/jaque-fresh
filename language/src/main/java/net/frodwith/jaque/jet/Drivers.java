package net.frodwith.jaque.jet;

import java.util.Optional;
import java.util.function.Function;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.dashboard.Location;

public final class Drivers {
  private final AstContext context;
  private final AxisMap<CallTarget> targets;

  public Drivers(AstContext context, Location location) {
    this.context = context;
    this.targets = context.getDrivers(location);
  }

  public Optional<CallTarget> getDriver(Iterable<Boolean> axis) {
    return Optional.ofNullable(targets.get(axis));
  }

  public boolean isValid(AstContext context) {
    return this.context.compatible(context);
  }
}
