package net.frodwith.jaque.data;

import java.util.Optional;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.exception.ExitException;

public final class NockObject {
  private final AstContext context;
  private final NockClass klass;
  private final AxisMap<CallTarget> drivers;

  public NockObject(NockClass klass, AstContext context) {
    this.klass = klass;
    this.context = context;
    this.drivers = klass.getDrivers(context);
  }

  public Assumption getAssumption() {
    return klass.valid;
  }

  public boolean locatedAt(Location location) {
    return klass.locatedAt(location);
  }

  public boolean compatible(AstContext context) {
    return this.context.compatible(context);
  }

  public boolean dashboardCompatible(Dashboard dashboard) {
    return context.dashboardCompatible(dashboard);
  }

  public boolean dashboardCompatible(AstContext context) {
    return context.dashboardCompatible(context);
  }

  public boolean copyableEdit(Axis written, Cell battery) {
    return klass.copyableEdit(written, battery);
  }

  public FineCheck getFine(Cell core) {
    return klass.getFine(core);
  }

  public NockObject recontextualize(Cell core, AstContext newContext) {
    return context.dashboardCompatible(newContext)
      ? new NockObject(klass, newContext)
      : newContext.getObject(core);
  }

  public Optional<Location> getLocation() {
    return klass.getLocation();
  }

  public CallTarget
    getArm(Cell core, Axis axis)
      throws ExitException {
    CallTarget t = drivers.get(axis);
    return ( null != t ) ? t :
      Cell.require(axis.fragment(core))
        .getMeta().getFunction(context).callTarget;
  }

  public CallTarget 
    getArm(Cell core, Axis axis, FragmentNode fragmentNode) 
      throws ExitException {
    CallTarget t = drivers.get(axis);
    return ( null != t ) ? t :
      Cell.require(fragmentNode.executeFragment(core))
        .getMeta().getFunction(context).callTarget;
  }
}
