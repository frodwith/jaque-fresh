package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public abstract class NockClass {
  public final Assumption valid;
  protected final Battery battery;
  private Optional<FineCheck> fine;

  protected NockClass(Battery battery, Assumption valid) {
    this.valid = valid;
    this.battery = battery;
    this.fine = Optional.empty();
  }

  public final boolean ofDashboard(Dashboard dashboard) {
    return battery.ofDashboard(dashboard);
  }

  public final FineCheck getFine(Cell core) {
    if ( fine.isPresent() ) {
      return fine.get();
    }
    else {
      CompilerDirectives.transferToInterpreter();
      try {
        FineCheck check = buildFine(core);
        fine = Optional.of(check);
        return check;
      }
      catch ( ExitException e ) {
        throw new AssertionError("Unexpected exit during fine build");
      }
    }
  }

  protected final NockFunction
    nockArm(Cell core, FragmentNode fragmentNode) throws ExitException {
    Cell formula = Cell.require(fragmentNode.executeFragment(core));
    return formula.getMeta().getFunction(formula, battery.dashboard);
  }

  protected abstract FineCheck buildFine(Cell core) throws ExitException;
  protected abstract Optional<NockFunction> getDriver(Axis axis);
  public abstract boolean copyableEdit(Axis written, Cell battery);
  public abstract boolean locatedAt(Location location);

  public NockFunction getArm(Cell core, Axis axis) throws ExitException {
    Optional<NockFunction> driver = getDriver(axis);
    return driver.isPresent()
      ? driver.get()
      : battery.getArm(Cell.require(core.head), axis.mas());
  }

  public NockFunction getArm(Cell core, Axis axis, FragmentNode fragmentNode)
    throws ExitException {
    Optional<NockFunction> driver = getDriver(axis);
    return driver.isPresent()
      ? driver.get()
      : battery.getArm(Cell.require(core.head), fragmentNode);
  }
}
