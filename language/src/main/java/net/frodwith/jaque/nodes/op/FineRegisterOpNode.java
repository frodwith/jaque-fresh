package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class FineRegisterOpNode extends RegisterOpNode {
  private final FastClue clue;
  private final FineCheck fine;

  public FineRegisterOpNode(FastClue clue, FineCheck fine,
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

    RegisterOpNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( fine.check(cc, dashboard) ) {
        return;
      }
      else {
        replacement =
          new StaticClueRegisterOpNode(this.clue, dashboard);
      }
    }
    else {
      replacement = new FullyDynamicRegisterOpNode(dashboard);
    }
    CompilerDirectives.transferToInterpreter();
    replace(replacement);
    replacement.executeRegister(core, clue);
  }
}
