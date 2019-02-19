package net.frodwith.jaque.data;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class FIXMEMetaObject implements TruffleObject {
  private final NockContext context;
  private final CellMeta meta;

  public FIXMEMetaObject(NockContext context, CellMeta meta) {
    this.context = context;
    this.meta = meta;
  }

  public ForeignAccess getForeignAccess() {
    return FIXMEMetaObjectMessageResolutionForeign.ACCESS;
  }

  public boolean hasObject() {
    return meta.hasObject();
  }

  public NockObject getObject() throws ExitException {
    return meta.getObject();
  }

  public NockFunction getFunction() throws ExitException {
    return meta.getFunction();
  }

  public NockFunction getArm(Axis axis) throws ExitException {
    return meta.getObject().getArm(axis, context);
  }

  public NockCall getCall(Axis axis) throws ExitException {
    NockFunction arm;
    if ( axis.inHead() ) {
      arm = meta.getObject().getArm(axis, context);
    }
    else {
      Object formula = axis.fragment(meta.cell);
      arm = Cell.require(formula).getMeta(context).getFunction();
    }
    return new NockCall(arm, meta.cell);
  }

  public FIXMEMetaObject edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(meta.cell, sample);
    return Cell.require(subject).getFIXMEMetaObject(context);
  }
}
