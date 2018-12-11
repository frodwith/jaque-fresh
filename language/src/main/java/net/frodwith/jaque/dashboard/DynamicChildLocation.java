package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.LocatedClass;
import net.frodwith.jaque.runtime.NockContext;

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
  public FineCheck buildFine(Cell core, NockContext context) {
    try {
      Cell battery = Cell.require(core.head);
      FineCheck parentFine = Cell.require(toParent.fragment(core))
        .getMeta(context).getObject().getFine(context);
      LocatedFine fine = (LocatedFine) parentFine;
      LocatedClass klass = (LocatedClass) core.getMeta(context).getObject().klass;
      return fine.addStep(new FineStep(battery, toParent, klass));
    }
    catch ( ExitException | ClassCastException e ) {
      // If you only pass me cores you know are located here, this will
      // never happen.
      CompilerDirectives.transferToInterpreter();
      throw new AssertionError();
    }
  }

  @Override
  public boolean copyableEdit(Axis axis) {
    return axis.inTail() && !axis.inside(toParent);
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
