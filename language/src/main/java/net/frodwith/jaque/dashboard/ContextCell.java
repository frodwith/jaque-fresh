package net.frodwith.jaque.dashboard;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class ContextCell implements TruffleObject {
  private final NockContext context;
  private final Cell cell;

  public ContextCell(NockContext context, Cell cell) {
    this.context = context;
    this.cell = cell;
  }

  private CellMeta meta() {
    return cell.getMeta();
  }

  public ForeignAccess getForeignAccess() {
    return ContextCellMessageResolutionForeign.ACCESS;
  }

  public boolean hasClass() {
    return meta().hasClass(context.astContext.dashboard);
  }

  public CallTarget getFunction() throws ExitException {
    return meta().getFunction(cell, context.astContext).callTarget;
  }

  public CallTarget getArm(Axis axis) throws ExitException {
    return meta().getNockClass(cell, context.astContext.dashboard)
      .getArm(cell, axis, context.astContext);
  }

  public NockCall getCall(Axis axis) throws ExitException {
    CallTarget arm;
    if ( axis.inHead() ) {
      arm = meta().getNockClass(cell, context.astContext.dashboard)
        .getArm(cell, axis, context.astContext);
    }
    else {
      Cell formula = Cell.require(axis.fragment(cell));
      arm = formula.getMeta().getFunction(formula, context.astContext).callTarget;
    }
    return new NockCall(arm, cell);
  }

  public ContextCell edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(cell, sample);
    return new ContextCell(context, Cell.require(subject));
  }
}
