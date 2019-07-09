package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Function;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.Cell;

import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.runtime.NockContext;

public abstract class UnlocatedClass extends NockClass {
  protected UnlocatedClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final boolean locatedAt(Location location) {
    return false;
  }

  @Override
  public final boolean copyableEdit(Object axisWritten, Cell batteryCell) {
    return battery.copyableEdit(axisWritten, batteryCell);
  }

  @Override
  public final CallTarget 
    getArm(Iterable<Boolean> axis, AstContext astContext, NockContext context, GetArm g)
      throws ExitException {
    return rawArm(astContext, context, g);
  }

  @Override
  public final Optional<Location> getLocation() {
    return Optional.empty();
  }
}
