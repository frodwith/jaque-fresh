package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.NockObject;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class FineRegistrationNode extends RegistrationNode {
  private final FastClue clue;
  private final FineCheck fine;

  public FineRegistrationNode(FastClue clue, FineCheck fine,
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

    RegistrationNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( fine.check(cc, contextReference.get()) ) {
        return core;
      }
      else {
        replacement =
          new StaticClueRegistrationNode(this.clue, contextReference);
      }
    }
    else {
      replacement = new FullyDynamicRegistrationNode(contextReference);
    }
    replace(replacement);
    return replacement.executeRegister(core, clue);
  }
}
