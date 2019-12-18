package net.frodwith.jaque.nodes.jet.ut;

import net.frodwith.jaque.nodes.NockNode;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class KeyNode extends NockNode {
  public abstract Object executeKey(VirtualFrame frame);
}
