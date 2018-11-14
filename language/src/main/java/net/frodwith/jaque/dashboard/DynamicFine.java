package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.data.Cell;

public final class DynamicFine extends LocatedFine {
  private final FineStep[] steps;
  private final StaticFine root;

  public DynamicFine(FineStep[] steps, StaticFine root) {
    this.steps = steps;
    this.root = root;
  }

  @Override
  public FineCheck addStep(FineStep step) {
    FineStep[] more = new FineStep[steps.length+1];
    more[0] = step;
    for ( int i = 0; i < steps.length; ++i ) {
      more[i+1] = steps[i];
    }
    return new DynamicFine(more, root);
  }

  @ExplodeLoop
  @Override
  public boolean check(Cell core, Supplier<Dashboard> supply) {
    final Cell[] cores = new Cell[steps.length];
    try {
      int i;

      for ( i = 0; i < steps.length; ++i ) {
        cores[i] = core;
        if ( !steps[i].check(core) ) {
          return false;
        }
        else {
          core = steps[i].toParent(core);
        }
      }

      if ( !root.check(core, supply) ) {
        return false;
      }

      for ( i = 0; i < steps.length; ++i ) {
        cores[i].setLocation(steps[i].location);
      }

      return true;
    }
    catch ( ExitException e ) {
      return false;
    }
  }
}
