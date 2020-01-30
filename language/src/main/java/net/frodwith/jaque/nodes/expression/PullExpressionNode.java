package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.nodes.op.PullOpNode;
import net.frodwith.jaque.nodes.op.PullOpNodeGen;

public abstract class PullExpressionNode extends NockExpressionNode {
  protected @Child @Executed NockExpressionNode coreNode;
  protected @Child @Executed(with="coreNode") PullOpNode pullNode;

  protected PullExpressionNode(NockExpressionNode coreNode,
                               Axis armAxis,
                               AstContext astContext,
                               boolean tailPosition) {
    this.coreNode = coreNode;
    this.pullNode = PullOpNodeGen.create(astContext, armAxis, tailPosition);
  }

  @Specialization
  protected Object dispatch(Object core, Object product) {
    return product;
  }
}
