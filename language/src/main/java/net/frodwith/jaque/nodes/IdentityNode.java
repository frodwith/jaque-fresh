package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;

public final class IdentityNode extends NockExpressionNode {
  public Object executeGeneric(VirtualFrame frame) {
    return NockLanguage.getSubject(frame);
  }
}
