package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.nodes.NockExpressionNode;

@NodeChild(value="body", type=NockExpressionNode.class)
@NodeField(name="dashboard", type=Dashboard.class)
public abstract class CoreNode extends NockExpressionNode {
  protected abstract Dashboard getDashboard();

  @Specialization
  protected Cell doCell(Cell core) {
    try {
      // called for side effect
      core.getMeta().getNockClass(core, getDashboard());
    }
    catch ( ExitException e ) {
    }
    return core;
  }

  @Fallback
  protected Object doObject(Object object) {
    return object;
  }
}
