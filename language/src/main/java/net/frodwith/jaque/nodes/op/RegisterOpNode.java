package net.frodwith.jaque.nodes.op;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.nodes.NockNode;

public abstract class RegisterOpNode extends NockNode {
  protected final Dashboard dashboard;

  protected RegisterOpNode(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

  public abstract void executeRegister(Object core, Object clue);

  protected final void register(Cell core, FastClue clue) throws ExitException {
    dashboard.register(core, clue);
  }
}
