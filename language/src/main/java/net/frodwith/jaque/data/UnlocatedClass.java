package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.exception.ExitException;

public abstract class UnlocatedClass extends NockClass {
  protected UnlocatedClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final NockFunction 
    getArm(Axis axis, FragmentNode fragment, 
           Supplier<NockFunctionRegistry> supply)
      throws ExitException {
    return battery.getArm(fragment, supply);
  }
}
