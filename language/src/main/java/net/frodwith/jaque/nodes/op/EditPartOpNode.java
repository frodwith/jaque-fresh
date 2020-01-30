package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.NockException;

public abstract class EditPartOpNode extends EditOpNode {
  private final Axis editAxis;
  private final Dashboard dashboard;
  protected @Child EditOpNode nextNode;

  protected EditPartOpNode(Axis editAxis, Dashboard dashboard) {
    this.editAxis = editAxis;
    this.dashboard = dashboard;
    this.nextNode = EditOpNode.fromAxis(editAxis.mas(), dashboard);
  }

  protected abstract Cell executePart(Cell whole, Object part);

  @Override
  public final Object executeEdit(Object whole, Object part) {
    if ( whole instanceof Cell ) {
      Cell proto  = (Cell) whole,
           mutant = executePart(proto, part);
      proto.copyMetaToMutant(mutant, editAxis, dashboard);
      return mutant;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      return new NockException("atomic edit", this);
    }
  }
}
