package net.frodwith.jaque.location;

import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.data.Cell;

public final class FineCheck {
  private final FineStep[] steps;
  private final Cell root;

  public FineCheck(FineStep[] steps, Cell root) {
    this.steps = steps;
    this.root = root;
  }

  @ExplodeLoop
  public boolean check(Cell core) {
    try {
      for ( FineStep s : steps ) {
        if ( !Equality.equals(s.battery, core.head) ) {
          return false;
        }
        core = Cell.require(s.toParent.fragment(core));
      }
      return Equality.equals(root, core);
    }
    catch ( ExitException e ) {
      return false;
    }
  }
}
