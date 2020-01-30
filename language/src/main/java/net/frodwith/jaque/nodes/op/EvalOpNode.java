package net.frodwith.jaque.nodes.op;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.nodes.dispatch.DispatchNode;
import net.frodwith.jaque.nodes.dispatch.TailDispatchNode;
import net.frodwith.jaque.nodes.dispatch.HeadDispatchNodeGen;

public final class EvalOpNode extends NockNode {
  private @Child FormulaLookupOpNode lookupNode;
  private @Child DispatchNode dispatchNode;

  public EvalOpNode(AstContext astContext, boolean tailPosition) {
    this.lookupNode = FormulaLookupOpNodeGen.create(astContext);
    this.dispatchNode = DispatchNode.create(tailPosition);
  }

  public Object executeNock(Object subject, Object formula) {
    return dispatchNode.executeDispatch(
      lookupNode.executeLookup(formula), subject);
  }
}
