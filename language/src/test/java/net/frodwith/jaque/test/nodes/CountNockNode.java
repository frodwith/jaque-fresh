package net.frodwith.jaque.test.nodes;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;

import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;

import net.frodwith.jaque.nodes.IdentityNode;
import net.frodwith.jaque.nodes.expression.EvalExpressionNode;
import net.frodwith.jaque.nodes.expression.EvalExpressionNodeGen;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;

public final class CountNockNode extends SubjectNode {
  private final AstContext astContext;
  private final Axis armAxis;
  private final String countName;
  private static final Map<String,Integer> counts = new HashMap<>();
  private @Child EvalExpressionNode evalNode;

  public CountNockNode(AstContext astContext,
                       Axis armAxis,
                       String countName) {
    this.astContext = astContext;
    this.armAxis = armAxis;
    this.countName = countName;
    this.evalNode = EvalExpressionNodeGen.create(
      new IdentityNode(), new SlotExpressionNode(armAxis), astContext, true);
  }

  private void bump() {
    counts.put(countName, count(countName) + 1);
  }

  public Object executeGeneric(VirtualFrame frame) {
    bump();
    return evalNode.executeGeneric(frame);
  }

  public static int count(String name) {
    return counts.containsKey(name) ? counts.get(name) : 0;
  }
}
