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


  protected abstract FineCheck buildFine(Cell core) throws ExitException;
  public abstract boolean copyableEdit(Axis written, Cell battery);
  public abstract boolean locatedAt(Location location);
  public abstract AxisMap<CallTarget> getDrivers(AstContext context);
  public abstract Optional<Location> getLocation();
}
