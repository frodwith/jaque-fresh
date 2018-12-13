package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.FastClue;
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

  protected Object executeRegister(Object core, Object clue) {
    RegistrationNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( Equality.equals(this.core, core) ) {
        return this.core;
      }
      else {
        NockContext context = contextReference.get();
        NockObject object;
        try {
          object = this.core.getMeta(context).getObject();
          replacement = new FineRegistrationNode(
              this.clue, object.getFine(context),
              contextReference);
        }
        catch ( ExitException e ) {
          // XX log non-core registration
          return core;
        }
      }
    }
    else {
      replacement = new FullyDynamicRegistrationNode(contextReference);
    }
    CompilerDirectives.transferToInterpreter();
    replace(replacement);
    return replacement.executeRegister(core, clue);
  }
}
