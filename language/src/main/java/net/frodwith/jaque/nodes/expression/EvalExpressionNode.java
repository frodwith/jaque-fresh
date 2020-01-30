package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.nodes.op.EvalOpNode;
import net.frodwith.jaque.nodes.NockExpressionNode;

public abstract class EvalExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode subjectNode;
  protected @Child @Executed NockExpressionNode formulaNode;
  protected @Child @Executed(with={"subjectNode", "formulaNode"}) EvalOpNode evalNode;

  protected EvalExpressionNode(NockExpressionNode subjectNode,
                               NockExpressionNode formulaNode,
                               AstContext astContext,
                               boolean tailPosition) {
    this.subjectNode = subjectNode;
    this.formulaNode = formulaNode;
    this.evalNode = new EvalOpNode(astContext, tailPosition);
  }

  @Specialization
  protected Object dispatch(Object subject, Object formula, Object product) {
    return product;
  }
}
