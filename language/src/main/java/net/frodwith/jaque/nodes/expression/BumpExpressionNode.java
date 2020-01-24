package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.op.BumpOpNode;
import net.frodwith.jaque.nodes.op.BumpOpNodeGen;
import net.frodwith.jaque.nodes.NockExpressionNode;

public abstract class BumpExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode expressionNode;
  protected @Child @Executed(with="expressionNode") BumpOpNode opNode;

  protected BumpExpressionNode(NockExpressionNode expressionNode) {
    this.expressionNode = expressionNode;
    this.opNode = BumpOpNodeGen.create();
  }

  @Specialization
  protected long direct(long input, long output) {
    return output;
  }

  @Specialization
  protected BigAtom indirect(Object input, BigAtom output) {
    return output;
  }
}
