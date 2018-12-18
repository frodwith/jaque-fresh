package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class FullyDynamicRegistrationNode extends RegistrationNode {

  public FullyDynamicRegistrationNode(
      ContextReference<NockContext> contextReference) {
    super(contextReference);
  }

  protected void executeRegister(Object core, Object clue) {
    try {
      register(Cell.require(core), FastClue.parse(clue));
    }
    catch ( ExitException e ) {
    }
  }
}
