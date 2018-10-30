package net.frodwith.jaque.data;

import java.util.Map;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.location.Location;

import net.frodwith.jaque.nodes.FragmentNode;

import net.frodwith.jaque.runtime.NockFunctionRegistry;

import net.frodwith.jaque.exception.ExitException;

public final class NockObject {
  public final Cell cell;
  public final Battery battery;
  public final Location location;
  public final AxisMap<NockFunction> drivers;
  public final Assumption valid;

  public NockObject(Cell cell,
                    Battery battery,
                    Location location,
                    AxisMap<NockFunction> drivers,
                    Assumption stable) {
    this.cell = cell;
    this.battery = battery;
    this.location = location;
    this.drivers = drivers;
    this.valid = stable;
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

  public boolean copyableEdit(Axis written) {
    if ( written.inHead() ) {
      return false;
    }
    else if ( battery.registry == null ) {
      return true;
    }
    else if ( null == location ) {
      return false;
    }
    else {
      return !written.inside(location.toParent);
    }
  }

  public NockObject like(Cell cell) {
    return new NockObject(cell, battery, valid, drivers, location);
  }
}
