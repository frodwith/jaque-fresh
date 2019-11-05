package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;

@NodeChildren({
    @NodeChild(value="cor", type=SlotNode.class),
    @NodeChild(value="gol", type=SlotNode.class),
    @NodeChild(value="gen", type=SlotNode.class),
    @NodeChild(value="van", type=SlotNode.class),
})
public abstract class MintNode extends SubjectNode {
  @Specialization
  protected Object mint(Object cor,
                        Object gol,
                        Object gen,
                        Object van) {
    System.err.println("Made it to mint!");
    System.exit(1);
    return null;
    //    return HoonMath.add(a, b);
  }
}
