package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Location;

public abstract class UnlocatedClass extends NockClass {
  protected UnlocatedClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final NockFunction 
    getArm(Axis axis, FragmentNode fragment, NockContext context)
      throws ExitException {
    return battery.getArm(fragment, context);
  }

  @Override
  public final boolean locatedAt(Location location) {
    return false;
  }
}
