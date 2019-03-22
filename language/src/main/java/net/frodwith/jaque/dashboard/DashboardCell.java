package net.frodwith.jaque.dashboard;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.exception.ExitException;

public final class DashboardCell implements TruffleObject {
  private final Dashboard dashboard;
  private final Cell cell;

  public DashboardCell(Dashboard dashboard, Cell cell) {
    this.dashboard = dashboard;
    this.cell = cell;
  }

  private CellMeta meta() {
    return cell.getMeta();
  }

  public ForeignAccess getForeignAccess() {
    return DashboardCellMessageResolutionForeign.ACCESS;
  }

  public boolean hasClass() {
    return meta().hasClass(dashboard);
  }

  public CallTarget getFunction() throws ExitException {
    return meta().getFunction(cell, dashboard);
  }

  public CallTarget getArm(Axis axis) throws ExitException {
    return meta().getClass(cell, dashboard).getArm(cell, axis);
  }

  public NockCall getCall(Axis axis) throws ExitException {
    CallTarget arm;
    if ( axis.inHead() ) {
      arm = meta().getClass(cell, dashboard).getArm(cell, axis);
    }
    else {
      Cell formula = Cell.require(axis.fragment(cell));
      arm = formula.getMeta().getFunction(formula, dashboard);
    }
    return new NockCall(arm, cell);
  }

  public DashboardCell edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(cell, sample);
    return new DashboardCell(dashboard, Cell.require(subject));
  }
}
