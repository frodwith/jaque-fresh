package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.op.DeepOpNode;
import net.frodwith.jaque.nodes.op.DeepOpNodeGen;

public abstract class DeepExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode expressionNode;
  protected @Child @Executed(with="expressionNode") DeepOpNode opNode;

  protected DeepExpressionNode(NockExpressionNode expressionNode) {
    this.expressionNode = expressionNode;
    this.opNode = DeepOpNodeGen.create();
  }

  @Specialization
  protected long test(Object noun, boolean deep) {
    return deep ? 0L : 1L;
  }
}
