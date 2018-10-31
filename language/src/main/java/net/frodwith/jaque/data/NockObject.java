package net.frodwith.jaque.data;

import java.util.Map;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.location.Location;

import net.frodwith.jaque.nodes.FragmentNode;

import net.frodwith.jaque.runtime.NockFunctionRegistry;

import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.location.FineCheck;

public final class NockObject {
  public final Cell cell;
  public final Battery battery;
  public final Location location;
  public final AxisMap<NockFunction> drivers;
  public final Assumption valid;

  private FineCheck fine;

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
    this.fine = null;
  }

  public NockFunction
    getArm(Axis axis, FragmentNode fragment, 
           Supplier<NockFunctionRegistry> functions)
      throws ExitException {
    NockFunction f;
    if ( null == drivers ||
         null == (f = drivers.get(axis)) ) {
      f = Cell.require(fragment.executeFragment(cell))
        .getMeta().getFunction(functions);
    }
    return f;
  }

  public FineCheck getFine() {
    if ( null == fine ) {
      fine = (null == location)
        ? FineCheck.unlocated(battery)
        : location.buildFine(cell);
    }
    return fine;
  }

  public boolean copyableEdit(Cell edited, Axis written) {
    if ( written.inHead() ) {
      return false;
    }
    else if ( battery.registration == null ) {
      return true;
    }
    else if ( null == location ) {
      return false;
    }
    else if ( written.inside(location.toParent) ) {
      return getFine().check(edited);
    }
    else {
      return true;
    }
  }

  public NockObject like(Cell cell) {
    return new NockObject(cell, battery, valid, drivers, location);
  }
}
