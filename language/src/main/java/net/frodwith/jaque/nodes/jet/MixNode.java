package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;

@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class),
  @NodeChild(value="b", type=SlotNode.class)
})
public abstract class MixNode extends SubjectNode {
  @Specialization
  protected long mix(long a, long b) throws ArithmeticException {
    return HoonMath.mix(a, b);
  }

  @Fallback
  protected Object mix(Object a, Object b) {
    return HoonMath.mix(needAtom(a), needAtom(b));
  }
}
