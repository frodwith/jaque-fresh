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
  public static final Fine NEVER = new Fine();

  public NockObject(Cell cell,
                    Assumption stable,
                    Location location) {
    this.cell = cell;
    this.valid = stable;
    this.location = location;
  }

  public NockFunction getArm(Object axis,
                             NockFunctionRegistry functions,
                             FragmentNode fragment) throws ExitException {
    NockFunction f;
    if ( null == location ||
         null == location.drivers ||
         null == (f = location.drivers.get(new Axis(axis))) ) {
      f = Cell.require(fragment.executeFragment(cell))
        .getMeta().getFunction(functions);
    }
    return f;
  }

  public boolean outsideParent(Object axis) {
    return (null == location)
      || (null == location.axisToParent) 
      || !Axis.subAxis(axis, location.axisToParent);
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
