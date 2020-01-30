package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;

public final class IdentityExpressionNode extends NockExpressionNode {
  public Object executeGeneric(VirtualFrame frame) {
    return NockLanguage.getSubject(frame);
  }
}
