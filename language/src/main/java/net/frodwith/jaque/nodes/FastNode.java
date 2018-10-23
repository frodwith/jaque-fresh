package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class FastNode extends NockExpressionNode {
  private @Child NockExpressionNode hintNode;
  private @Child NockExpressionNode nextNode;
  private @Child RegistrationNode registerNode;

  public FastNode(NockExpressionNode hintNode, NockExpressionNode nextNode) {
    this.hintNode = hintNode;
    this.nextNode = nextNode;
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object clue = hintNode.executeGeneric(frame);
    Object core = nextNode.executeGeneric(frame);
    registerNode.executeRegister(core, clue);
    return core;
  }
}
