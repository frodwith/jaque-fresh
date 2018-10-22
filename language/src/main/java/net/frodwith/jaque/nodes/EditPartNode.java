package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class EditPartNode extends NockNode {
  public abstract Object executeEdit(VirtualFrame frame, Object large);
}
