package net.frodwith.jaque.nodes.jet;

import net.frodwith.jaque.nodes.SubjectNode;

public abstract class JetNode extends SubjectNode {
  protected abstract SubjectNode[] createInputNodes();
}
