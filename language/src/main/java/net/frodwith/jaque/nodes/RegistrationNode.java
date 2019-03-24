package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;

public abstract class RegistrationNode extends NockNode {
  protected final Dashboard dashboard;

  protected RegistrationNode(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

  public abstract void executeRegister(Object core, Object clue);

  protected final void register(Cell core, FastClue clue) throws ExitException {
    dashboard.register(core, clue);
  }
}
