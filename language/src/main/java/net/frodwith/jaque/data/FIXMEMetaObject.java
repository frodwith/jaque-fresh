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

  public boolean hasObject() {
    return meta().hasObject();
  }

  public NockObject getObject() throws ExitException {
    return meta().getObject(context, cell);
  }

  public NockFunction getFunction() throws ExitException {
    return meta().getFunction(context, cell);
  }

  public NockFunction getArm(Axis axis) throws ExitException {
    return meta().getObject(context, cell).getArm(axis, context);
  }

  public NockCall getCall(Axis axis) throws ExitException {
    NockFunction arm;
    if ( axis.inHead() ) {
      arm = meta().getObject(context, cell).getArm(axis, context);
    }
    else {
      Cell formula = Cell.require(axis.fragment(cell));
      arm = formula.getMeta().getFunction(context, formula);
    }
    return new NockCall(arm, cell);
  }

  public FIXMEMetaObject edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(cell, sample);
    return Cell.require(subject).getFIXMEMetaObject(context);
  }
}
