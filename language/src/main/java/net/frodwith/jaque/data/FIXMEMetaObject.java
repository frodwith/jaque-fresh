package net.frodwith.jaque.data;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class FIXMEMetaObject implements TruffleObject {
  private final NockContext context;
  private final Cell cell;

  public FIXMEMetaObject(NockContext context, Cell cell) {
    this.context = context;
    this.cell = cell;
  }

  private CellMeta meta() {
    return cell.getMeta();
  }

  public ForeignAccess getForeignAccess() {
    return FIXMEMetaObjectMessageResolutionForeign.ACCESS;
  }

  public boolean hasClass() {
    return meta().hasClass(context.dashboard);
  }

  public NockFunction getFunction() throws ExitException {
    return meta().getFunction(cell, context.dashboard);
  }

  public NockFunction getArm(Axis axis) throws ExitException {
    return meta().getClass(cell, context.dashboard).getArm(cell, axis);
  }

  public NockCall getCall(Axis axis) throws ExitException {
    NockFunction arm;
    if ( axis.inHead() ) {
      arm = meta().getClass(cell, context.dashboard).getArm(cell, axis);
    }
    else {
      Cell formula = Cell.require(axis.fragment(cell));
      arm = formula.getMeta().getFunction(formula, context.dashboard);
    }
    return new NockCall(arm, cell);
  }

  public FIXMEMetaObject edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(cell, sample);
    return Cell.require(subject).getFIXMEMetaObject(context);
  }
}
