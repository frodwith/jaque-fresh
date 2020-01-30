package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Executed;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.nodes.op.EditOpNode;

public abstract class EditExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode wholeNode;
  protected @Child @Executed NockExpressionNode partNode;
  protected @Child @Executed(with={"wholeNode", "partNode"}) EditOpNode editNode;

  public EditExpressionNode(NockExpressionNode wholeNode,
                            NockExpressionNode partNode,
                            Axis axis,
                            Dashboard dashboard) {
    this.wholeNode = wholeNode;
    this.partNode = partNode;
    this.editNode = EditOpNode.fromAxis(axis, dashboard);
  }

  @Specialization
  protected Object edit(Object whole, Object part, Object mutant) {
    return mutant;
  }
}
