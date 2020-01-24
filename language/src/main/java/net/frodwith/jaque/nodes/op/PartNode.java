package net.frodwith.jaque.nodes.op;

import net.frodwith.jaque.nodes.NockNode;

public abstract class PartNode extends NockNode {
  public abstract Object executePart(Object cell);
}
