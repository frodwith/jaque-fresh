package net.frodwith.jaque.dashboard;

import java.util.Optional;
import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

public final class StaticFine extends LocatedFine {
  private final Cell staticNoun;
  private final LocatedClass klass;

  public StaticFine(Cell staticNoun, LocatedClass klass) {
    this.staticNoun = staticNoun;
    this.klass = klass;
  }

  @Override
  public boolean check(Cell core, Dashboard dashboard) {
    CellMeta meta = core.getMeta();
    Optional<NockClass> cachedClass = meta.cachedClass(dashboard);
    if ( cachedClass.isPresent() ) {
      return cachedClass.get().locatedAt(klass.location);
    }
    else if ( Equality.equals(core, staticNoun) ) {
      meta.setNockClass(klass);
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public FineCheck addStep(FineStep step) {
    return new DynamicFine(new FineStep[] { step }, this);
  }
}
