package net.frodwith.jaque.data;

import java.util.function.Supplier;

import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// NockClass contains all the relevant dispatch information, and our (lazy,
// cached) fine check tells us if another object can share our class.
public final class NockObject {
  public final NockClass klass;
  public final Cell noun;
  private FineCheck fine;

  private NockObject(NockClass klass, Cell noun, FineCheck fine) {
    this.klass = klass;
    this.noun = noun;
    this.fine = fine;
  }

  public NockObject(NockClass klass, Cell noun) {
    this(klass, noun, null);
  }

  public FineCheck getFine(Supplier<Dashboard> supply) {
    if ( null == fine ) {
      try {
        fine = klass.getFine(noun, supply);
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
}
