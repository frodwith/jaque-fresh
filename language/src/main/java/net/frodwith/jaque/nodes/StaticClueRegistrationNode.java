package net.frodwith.jaque.nodes;

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

  protected Object executeRegister(Object core, Object clue) {
    Cell cc;
    try {
      cc = Cell.require(core);
    }
    catch ( ExitException e ) {
      return core;
    }

    if ( Equality.equals(this.clue.noun, clue) ) {
      register(cc, this.clue);
      return core;
    }
    else {
      RegistrationNode replacement = new FullyDynamicRegistrationNode(contextReference);
      replace(replacement);
      return replacement.executeRegister(core, clue);
    }
  }
}
