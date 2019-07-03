package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Function;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.parser.FormulaParser;
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

  public final boolean isValid(Dashboard dashboard) {
    return valid.isValid() && ofDashboard(dashboard);
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

  @FunctionalInterface
  protected static interface GetArm {
    public Object get() throws ExitException;
  }

  protected abstract CallTarget
    getArm(Axis axis, AstContext context, GetArm g)
      throws ExitException;

  protected final CallTarget 
    rawArm(AstContext context, GetArm g)
      throws ExitException {
    Cell formula = Cell.require(g.get());
    return formula.getMeta().getFunction(formula, context).callTarget;
  }
 
  public final CallTarget
    getArm(Cell core, Axis axis, AstContext context)
      throws ExitException {
    return getArm(axis, context, () -> axis.fragment(core));
  }

  // if you have a FragmentNode, we can use it
  public final CallTarget
    getArm(Cell core, Axis axis, FragmentNode fragmentNode, AstContext context)
      throws ExitException {
    return getArm(axis, context, () -> fragmentNode.executeFragment(core));
  }

  protected abstract FineCheck buildFine(Cell core) throws ExitException;
  public abstract boolean copyableEdit(Object axisWritten, Cell battery);
  public abstract boolean locatedAt(Location location);
  public abstract Optional<Location> getLocation();
}
