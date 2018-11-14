package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.exception.ExitException;

public abstract class NockClass {
  public final Battery battery;
  public final Assumption valid;

  protected NockClass(Battery battery, Assumption stable) {
    this.battery = battery;
    this.valid = stable;
  }

  public abstract NockFunction 
    getArm(Axis axis, FragmentNode fragment,
           Supplier<NockFunctionRegistry> supply)
      throws ExitException;

  public abstract FineCheck 
    getFine(Cell core, Supplier<Dashboard> supply)
      throws ExitException;

  public abstract boolean copyableEdit(Axis written);
}
