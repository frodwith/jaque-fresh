package net.frodwith.jaque.data;

import java.util.function.Supplier;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// NockClass contains all the relevant dispatch information, and our (lazy,
// cached) fine check tells us if another object can share our class.
public final class NockObject {
  public final Cell noun;
  private final NockClass klass;
  private FineCheck fine;

  private NockObject(NockClass klass, Cell noun, FineCheck fine) {
    this.klass = klass;
    this.noun = noun;
    this.fine = fine;
  }

  public NockObject(NockClass klass, Cell noun) {
    this(klass, noun, null);
  }

  public FineCheck getFine(NockContext context) {
    if ( null == fine ) {
      try {
        fine = klass.getFine(noun, context);
      }
      catch ( ExitException e ) {
        // the noun got us here in the first place, can't happen
        throw new AssertionError();
      }
    }
    return fine;
  }

  public NockObject like(Cell core) {
    return new NockObject(klass, core, fine);
  }

  public NockFunction getArm(Axis arm, NockContext context)
    throws ExitException {
    return klass.getArm(arm, context);
  }

  public NockFunction
    getArm(Axis axis, FragmentNode fragmentNode, NockContext context)
      throws ExitException {
    return klass.getArm(axis, fragmentNode, context);
  }

  public boolean isValid(Dashboard dashboard) {
    return klass.isValid(dashboard);
  }
}
