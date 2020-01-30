package net.frodwith.jaque.nodes.op;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.dashboard.Dashboard;

final class EditTailOpNode extends EditPartOpNode {
  EditTailOpNode(Axis axis, Dashboard dashboard) {
    super(axis, dashboard);
  }

  @Override
  protected Cell executePart(Cell whole, Object part) {
    return new Cell(whole.head, nextNode.executeEdit(whole.tail, part));
  }
}
