package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.NockCall;

public abstract class NockCallLookupNode extends NockNode {
  public abstract NockCall executeLookup(VirtualFrame frame);
}
