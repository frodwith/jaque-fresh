package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

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
  public boolean check(Cell core, NockContext context) {
    final Cell[] cores = new Cell[steps.length];
    try {
      int i;

      for ( i = 0; i < steps.length; ++i ) {
        cores[i] = core;
        FineStep step = steps[i];
        if ( step.whole(core) ) {
          return true;
        }
        else if ( !step.part(core) ) {
          return false;
        }
        else {
          core = step.next(core);
        }
      }

      if ( !root.check(core, context) ) {
        return false;
      }

      for ( i = 0; i < steps.length; ++i ) {
        steps[i].save(cores[i], context);
      }

      return true;
    }
    catch ( ExitException e ) {
      return false;
    }
  }
}
