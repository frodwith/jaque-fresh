package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.op.SameOpNode;
import net.frodwith.jaque.nodes.op.SameOpNodeGen;

public abstract class SameExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode leftNode;
  protected @Child @Executed NockExpressionNode rightNode;
  protected @Child @Executed(with={"leftNode", "rightNode"}) SameOpNode sameNode;

  protected SameExpressionNode(NockExpressionNode left, NockExpressionNode right) {
    this.leftNode = left;
    this.rightNode = right;
    this.sameNode = SameOpNodeGen.create();
  }

  @Specialization
  protected long test(Object left, Object right, boolean same) {
    return same ? 0L : 1L;
  }
}
