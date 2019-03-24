package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class StaticRegistrationNode extends RegistrationNode {
  private final Cell core;
  private final FastClue clue;

  public StaticRegistrationNode(Cell core, FastClue clue,
    Dashboard dashboard) {
    super(dashboard);
    this.core = core;
    this.clue = clue;
  }

  @Override
  public void executeRegister(Object core, Object clue) {
    RegistrationNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( Equality.equals(this.core, core) ) {
        return;
      }
      else {
        NockClass klass;
        try {
          klass = this.core.getMeta().getNockClass(this.core, dashboard);
          replacement = new FineRegistrationNode(
              this.clue, klass.getFine(this.core),
              dashboard);
        }
        catch ( ExitException e ) {
          // XX log non-core registration
          return;
        }
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
