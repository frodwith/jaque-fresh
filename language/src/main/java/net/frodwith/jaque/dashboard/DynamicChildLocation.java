package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.Objects;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;

import net.frodwith.jaque.exception.ExitException;

public final class DynamicChildLocation extends Location {
  public final Location parent;
  public final Axis toParent;

  public DynamicChildLocation(String name,
                              Map<String,Hook> hooks,
                              Location parent,
                              Axis toParent) {
    super(name, hooks);
    this.parent = parent;
    this.toParent = toParent;
  }

  @Override
  public FineCheck buildFine(Cell core, Dashboard dashboard) {
    try {
      Cell battery = Cell.require(core.head);
      LocatedClass klass = (LocatedClass)
        core.getMeta().getNockClass(core, dashboard);

      Cell parentCore = Cell.require(toParent.fragment(core));
      FineCheck parentFine = parentCore.getMeta()
        .getNockClass(parentCore, dashboard).getFine(parentCore);

      return ((LocatedFine) parentFine)
        .addStep(new FineStep(battery, toParent, klass));
    }
    catch ( ExitException | ClassCastException e ) {
      // If you only pass me cores you know are located here, this will
      // never happen.
      CompilerDirectives.transferToInterpreter();
      throw new AssertionError();
    }
  }

  @Override
  public boolean copyableEdit(Object axisWritten) {
    return false;
    /* FIXME: need a copyableEditNode to do non-slow-path math on axisWritten.
       returning dummy false for now.
    return axis.inTail() && !axis.inside(toParent);
    */
  }

  @Override
  public boolean equals(Object o) {
    if ( !(o instanceof DynamicChildLocation) ) {
      return false;
    }
    DynamicChildLocation dcl = (DynamicChildLocation) o;
    return name.equals(dcl.name) 
      && hooks.equals(dcl.hooks)
      && toParent.equals(dcl.toParent)
      && parent.equals(dcl.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, hooks, toParent, parent);
  }

  @Override
  public void register(Registration registration) {
    registration.registerChild(toParent, this, parent);
  }

  @Override
  public void audit(FastClue clue) {
    if ( !clue.toParent.equals(toParent) ) {
      LOG.warning(name + " axis mismatch: got " + clue.toParent.toString() +
          ", expected " + toParent.toString() + ".");
    }
    super.audit(clue);
  }
}
