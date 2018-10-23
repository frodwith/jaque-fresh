package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class DynamicCoreRegistrationNode extends RegistrationNode {
  private final FastClue clue;
  private final NockObject.Fine fine;

  public DynamicCoreRegistrationNode(FastClue clue, NockObject.Fine fine,
      ContextReference<NockContext> contextReference) {
    super(contextReference);
    this.clue = clue;
    this.fine = fine;
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
      if ( !fine.check(cc) ) {
        register(cc, this.clue);
      }
      return core;
    }
    else {
      RegistrationNode replacement = new
        FullyDynamicRegistrationNode(contextReference);
      replace(replacement);
      return replacement.executeRegister(core, clue);
    }
  }
}
