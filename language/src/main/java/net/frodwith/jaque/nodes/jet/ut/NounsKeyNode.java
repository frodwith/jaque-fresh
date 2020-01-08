package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;

import net.frodwith.jaque.NounsKey;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.nodes.SubjectNode;

public final class NounsKeyNode extends NockNode {
  private final String cacheId;
  private @Children SubjectNode[] nounNodes;

  public NounsKeyNode(String cacheId, SubjectNode[] nounNodes) {
    this.cacheId = cacheId;
    this.nounNodes = nounNodes;
  }

  @ExplodeLoop(kind = LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
  public NounsKey executeKey(VirtualFrame frame) {
    Object[] products = new Object[nounNodes.length];
    for ( int i = 0; i < products.length; ++i ) {
      products[i] = nounNodes[i].executeGeneric(frame);
    }
    return new NounsKey(cacheId, products);
  }
}
