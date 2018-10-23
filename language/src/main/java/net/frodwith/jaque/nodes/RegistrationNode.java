package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;

public abstract class RegistrationNode extends NockNode {
  protected final ContextReference<NockContext> contextReference;

  protected RegistrationNode(ContextReference<NockContext> contextReference) {
    this.contextReference = contextReference;
  }

  protected abstract Object executeRegister(Object core, Object clue);

  protected final void register(Cell core, FastClue clue) {
    contextReference.get().dashboard.register(core, clue);
  }
}
