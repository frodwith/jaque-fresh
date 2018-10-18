package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.exception.NockException;

public final class BailNode extends NockExpressionNode {
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    throw new NockException("!!", this);
  }
}
