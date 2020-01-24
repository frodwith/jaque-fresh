package net.frodwith.jaque.nodes.op;

import java.util.ArrayDeque;

import com.oracle.truffle.api.nodes.ExplodeLoop;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.NockNode;

public final class SlotOpNode extends NockNode {
  private @Children PartNode[] parts;

  private SlotOpNode(PartNode[] parts) {
    this.parts = parts;
  }

  @ExplodeLoop
  public Object executeSlot(Object noun) {
    for ( PartNode p : parts ) {
      noun = p.executePart(noun);
    }
    return noun;
  }

  public static SlotOpNode fromAxis(Axis a) {
    ArrayDeque<PartNode> tmp = new ArrayDeque<>();
    for ( boolean right : a ) {
      PartNode node = right ? TailOpNodeGen.create() : HeadOpNodeGen.create();
      tmp.add(node);
    }
    return new SlotOpNode(tmp.toArray(new PartNode[tmp.size()]));
  }
}
