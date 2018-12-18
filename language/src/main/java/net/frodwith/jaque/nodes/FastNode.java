package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.runtime.NockContext;

public final class FastNode extends NockExpressionNode {
  private @Child NockExpressionNode hintNode;
  private @Child NockExpressionNode nextNode;
  private @Child RegistrationNode registerNode;

  public FastNode(ContextReference<NockContext> contextReference,
      NockExpressionNode hintNode, NockExpressionNode nextNode) {
    this.hintNode = hintNode;
    this.nextNode = nextNode;
    this.registerNode = new InitialRegistrationNode(contextReference);
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object clue = hintNode.executeGeneric(frame);
    Object core = nextNode.executeGeneric(frame);
    registerNode.doRegistration(core, clue);
    return core;
  }
}
