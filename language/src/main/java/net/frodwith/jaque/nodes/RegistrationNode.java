package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;

public abstract class RegistrationNode extends NockNode {
  protected final ContextReference<NockContext> contextReference;

  protected RegistrationNode(ContextReference<NockContext> contextReference) {
    this.contextReference = contextReference;
  }

  protected abstract Object executeRegister(Object core, Object clue);

  protected final void register(Cell core, FastClue clue) throws ExitException {
    contextReference.get().dashboard.register(core, clue);
  }

  protected final Supplier<Dashboard> getSupplier() {
    return () -> contextReference.get().dashboard;
  }
}
