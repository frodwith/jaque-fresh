package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.nodes.op.RegisterOpNode;
import net.frodwith.jaque.nodes.op.InitialRegisterOpNode;

import java.io.StringWriter;
import net.frodwith.jaque.interop.InteropDebugDump;

public final class FastExpressionNode extends NockExpressionNode {
  private @Child NockExpressionNode hintNode;
  private @Child NockExpressionNode nextNode;
  private @Child RegisterOpNode registerNode;

  public FastExpressionNode(Dashboard dashboard,
      NockExpressionNode hintNode, NockExpressionNode nextNode) {
    this.hintNode = hintNode;
    this.nextNode = nextNode;
    this.registerNode = new InitialRegisterOpNode(dashboard);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object clue = hintNode.executeGeneric(frame);
    Object core = nextNode.executeGeneric(frame);

    // try {
    //   StringWriter w = new StringWriter();
    //   InteropDebugDump.debugDump(w, clue, false, 0, 5);
    //   System.err.println(w.toString());
    // } catch (Exception e) {}

    registerNode.executeRegister(core, clue);
    return core;
  }
}
