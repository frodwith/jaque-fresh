package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.op.SameOpNode;
import net.frodwith.jaque.nodes.op.SameOpNodeGen;
import net.frodwith.jaque.nodes.NockExpressionNode;

public abstract class SameExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode leftNode;
  protected @Child @Executed NockExpressionNode rightNode;
  protected @Child @Executed(with={"leftNode", "rightNode"}) SameOpNode opNode;

  protected SameExpressionNode(NockExpressionNode left, NockExpressionNode right) {
    this.leftNode = left;
    this.rightNode = right;
    this.opNode = SameOpNodeGen.create();
  }

  @Specialization
  protected long test(Object left, Object right, boolean same) {
    return same ? 0L : 1L;
  }
}
