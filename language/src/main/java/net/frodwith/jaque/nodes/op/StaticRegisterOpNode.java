package net.frodwith.jaque.nodes.op;

import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.exception.ExitException;

// A core we have already registred (noun-equal).
public final class StaticRegisterOpNode extends RegisterOpNode {
  private final Cell core;
  private final FastClue clue;

  public StaticRegisterOpNode(Cell core, FastClue clue, Dashboard dashboard) {
    super(dashboard);
    this.core = core;
    this.clue = clue;
  }

  @TruffleBoundary
  private RegisterOpNode buildFineReplacement(NockClass klass) {
    return new FineRegisterOpNode(
              this.clue, klass.getFine(this.core),
              dashboard);
  }

  @TruffleBoundary
  private RegisterOpNode buildFullyDynamic() {
    return new FullyDynamicRegisterOpNode(dashboard);
  }

  @Override
  public void executeRegister(Object core, Object clue) {
    RegisterOpNode replacement;
    if ( Equality.equals(this.clue.noun, clue) ) {
      if ( Equality.equals(this.core, core) ) {
        return;
      }
      else {
        NockClass klass;
        try {
          klass = this.core.getMeta().getNockClass(this.core, dashboard);
          replacement = buildFineReplacement(klass);
        }
        catch ( ExitException e ) {
          // XX log non-core registration
          return;
        }
      }
    }
    else {
      replacement = buildFullyDynamic();
    }
    CompilerDirectives.transferToInterpreter();
    replace(replacement);
    replacement.executeRegister(core, clue);
  }
}
