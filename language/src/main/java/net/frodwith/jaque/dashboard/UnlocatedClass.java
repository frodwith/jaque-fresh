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
    getArm(Axis axis, AstContext context, GetArm g)
      throws ExitException {
    return rawArm(context, g);
  }

  @Override
  public final Optional<Location> getLocation() {
    return Optional.empty();
  }
}
