package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.data.NockClass;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class StaticRegistrationNode extends RegistrationNode {
  private final Cell core;
  private final FastClue clue;

  public StaticRegistrationNode(Cell core, FastClue clue,
    ContextReference<NockContext> contextReference) {
    super(contextReference);
    this.core = core;
    this.clue = clue;
  }

  protected void executeRegister(Object core, Object clue) {
    RegistrationNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( Equality.equals(this.core, core) ) {
        return;
      }
      else {
        Dashboard dash = contextReference.get().dashboard;
        NockClass klass;
        try {
          klass = this.core.getMeta().getClass(this.core, dash);
          replacement = new FineRegistrationNode(
              this.clue, klass.getFine(this.core),
              contextReference);
        }
        catch ( ExitException e ) {
          // XX log non-core registration
          return;
        }
      }
    }
    else {
      replacement = new FullyDynamicRegistrationNode(contextReference);
    }
    CompilerDirectives.transferToInterpreter();
    replace(replacement);
    replacement.executeRegister(core, clue);
  }
}
