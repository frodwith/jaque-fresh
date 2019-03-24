package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class FineRegistrationNode extends RegistrationNode {
  private final FastClue clue;
  private final FineCheck fine;

  public FineRegistrationNode(FastClue clue, FineCheck fine,
      Dashboard dashboard) {
    super(dashboard);
    this.clue = clue;
    this.fine = fine;
  }

  public void executeRegister(Object core, Object clue) {
    Cell cc;
    try {
      cc = Cell.require(core);
    }
    catch ( ExitException e ) {
      return;
    }

    RegistrationNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( fine.check(cc, dashboard) ) {
        return;
      }
      else {
        replacement =
          new StaticClueRegistrationNode(this.clue, dashboard);
      }
    }
    else {
      replacement = new FullyDynamicRegistrationNode(dashboard);
    }
    CompilerDirectives.transferToInterpreter();
    replace(replacement);
    replacement.executeRegister(core, clue);
  }
}
