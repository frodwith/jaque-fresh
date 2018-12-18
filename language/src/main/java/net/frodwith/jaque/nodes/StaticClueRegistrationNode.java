package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class StaticClueRegistrationNode extends RegistrationNode {
  private final FastClue clue;

  public StaticClueRegistrationNode(FastClue clue,
      ContextReference<NockContext> contextReference) {
    super(contextReference);
    this.clue = clue;
  }

  protected void executeRegister(Object core, Object clue) {
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
      RegistrationNode replacement = new FullyDynamicRegistrationNode(contextReference);
      CompilerDirectives.transferToInterpreter();
      replace(replacement);
      replacement.executeRegister(core, clue);
    }
  }
}
