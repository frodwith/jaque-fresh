package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;

public final class FastNode extends NockExpressionNode {
  private @Child NockExpressionNode hintNode;
  private @Child NockExpressionNode nextNode;
  private @Child RegistrationNode registerNode;

  public FastNode(Dashboard dashboard,
      NockExpressionNode hintNode, NockExpressionNode nextNode) {
    this.hintNode = hintNode;
    this.nextNode = nextNode;
    this.registerNode = new InitialRegistrationNode(dashboard);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object clue = hintNode.executeGeneric(frame);
    Object core = nextNode.executeGeneric(frame);
    registerNode.executeRegister(core, clue);
    return core;
  }
}
