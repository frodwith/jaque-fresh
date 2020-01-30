package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.exception.NockException;

import net.frodwith.jaque.nodes.NockExpressionNode;

public final class BailExpressionNode extends NockExpressionNode {
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    throw new NockException("!!", this);
  }
}
