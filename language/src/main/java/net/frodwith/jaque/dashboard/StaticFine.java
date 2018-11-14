package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.runtime.Equality;

public final class StaticFine extends LocatedFine {
  private final Cell staticNoun;
  private final NockObject object;

  public StaticFine(Cell staticNoun, NockObject object) {
    this.staticNoun = staticNoun;
    this.object = object;
  }

  @Override
  public boolean check(Cell core, Supplier<Dashboard> supply) {
    if ( Equality.equals(core, staticNoun) ) {
      core.getMeta().setObject(object);
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
