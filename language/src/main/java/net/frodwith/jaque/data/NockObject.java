package net.frodwith.jaque.data;

import java.util.Map;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.nodes.FragmentNode;

import net.frodwith.jaque.runtime.Axis;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockFunctionRegistry;

import net.frodwith.jaque.exception.Fail;

public final class NockObject {
  public final Cell cell;
  public final Assumption valid;
  private final Map<Object,NockFunction> drivers;
  private final Object axisToParent;

  private NockObject(Cell cell,
                     Assumption stable,
                     Map<Object,NockFunction> drivers,
                     Object axisToParent) {
    this.cell = cell;
    this.valid = stable;
    this.drivers = drivers;
    this.axisToParent = axisToParent;
  }

  public NockObject(Cell cell, Assumption stable, Location location) {
    this(cell, stable,
        location == null ? null : location.drivers,
        location == null ? null : location.axisToParent);
  }

  @TruffleBoundary
  private NockFunction getDriver(Object axis) {
    return drivers.get(axis);
  }

  public NockFunction getArm(Object axis,
                             NockFunctionRegistry functions,
                             FragmentNode fragment) throws Fail {
    NockFunction f;
    if ( (null == drivers) || (null == (f = getDriver(axis))) ) {
      f = functions.lookup(Cell.require(fragment.executeFragment(cell)));
    }
    return f;
  }

  public boolean outsideParent(Object axis) {
    return (null == axisToParent) || !Axis.subAxis(axis, axisToParent);
  }

  public NockObject like(Cell cell) {
    return new NockObject(cell, valid, drivers, axisToParent);
  }
}
