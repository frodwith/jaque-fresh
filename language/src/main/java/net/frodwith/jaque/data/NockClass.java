package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public abstract class NockClass {
  public final Battery battery;
  public final Assumption valid;

  protected NockClass(Battery battery, Assumption stable) {
    this.battery = battery;
    this.valid = stable;
  }

  public abstract boolean locatedAt(Location location);

  public abstract NockFunction getArm(Axis axis, 
      Cell batteryCell, Dashboard dashboard) throws ExitException;

  public abstract NockFunction 
    getArm(Axis axis, FragmentNode fragment,
        Cell batteryCell, Dashboard dashboard)
      throws ExitException;

  public abstract FineCheck getFine(Cell core, NockContext context)
    throws ExitException;

  public abstract boolean copyableEdit(Cell batteryCell, Axis written);
}
