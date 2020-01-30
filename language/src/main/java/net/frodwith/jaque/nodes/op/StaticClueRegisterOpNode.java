package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

public final class StaticClueRegisterOpNode extends RegisterOpNode {
  private final FastClue clue;

  public StaticClueRegisterOpNode(FastClue clue, Dashboard dashboard) {
    super(dashboard);
    this.clue = clue;
  }

  public void executeRegister(Object core, Object clue) {
    Cell cc;
    try {
      cc = Cell.require(core);
    }
    catch ( ExitException e ) {
      return;
    }

    if ( Equality.equals(this.clue.noun, clue) ) {
      try {
        register(cc, this.clue);
      }
      catch ( ExitException e ) {
        // XX: log the failure
      }
    }
    else {
      RegisterOpNode replacement = new FullyDynamicRegisterOpNode(dashboard);
      CompilerDirectives.transferToInterpreter();
      replace(replacement);
      replacement.executeRegister(core, clue);
    }
  }
}
