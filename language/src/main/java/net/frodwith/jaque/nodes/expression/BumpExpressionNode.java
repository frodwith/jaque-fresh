package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.op.BumpOpNode;
import net.frodwith.jaque.nodes.op.BumpOpNodeGen;
import net.frodwith.jaque.nodes.NockExpressionNode;

public abstract class BumpExpressionNode extends NockExpressionNode {
  @Child @Executed NockExpressionNode expressionNode;
  @Child @Executed(with="expressionNode") BumpOpNode opNode;

  BumpExpressionNode(NockExpressionNode expressionNode) {
    this.expressionNode = expressionNode;
    this.opNode = BumpOpNodeGen.create();
  }

  @Specialization
  long direct(long input, long output) {
    return output;
  }

  @Specialization
  BigAtom indirect(Object input, BigAtom output) {
    return output;
  }
}
