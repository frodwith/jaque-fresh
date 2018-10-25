package net.frodwith.jaque.data;

import java.util.Map;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.nodes.FragmentNode;

import net.frodwith.jaque.runtime.NockFunctionRegistry;

import net.frodwith.jaque.exception.ExitException;

public final class NockObject {
  public final Cell cell;
  public final Assumption valid;
  public final Location location;
  public final AxisMap<NockFunction> drivers;
  public static final Fine NEVER = new Fine();

  public NockObject(Cell cell,
                    Assumption stable,
                    AxisMap<NockFunction> drivers,
                    Location location) {
    this.cell = cell;
    this.valid = stable;
    this.drivers = drivers;
    this.location = location;
  }

  public NockFunction
    getArm(Axis axis, FragmentNode fragment,
           ContextReference<NockContext> contextReference)
      throws ExitException {
    NockFunction f;
    if ( null == drivers ||
         null == (f = drivers.get(axis)) ) {
      f = Cell.require(fragment.executeFragment(cell)).getMeta()
        .getFunction(contextReference.get().functionRegistry);
    }
    return f;
  }

  public boolean outsideParent(Axis ax) {
    return (null != location)
      && (null != location.toParent)
      && !ax.inside(location.toParent);
  }

  public NockObject like(Cell cell) {
    return new NockObject(cell, valid, location);
  }

  public Fine createFine() {
    return NEVER;
  }

  public static final class Fine {
    public boolean check(Cell core) {
      // XX todo
      return false;
    }
  }
}
