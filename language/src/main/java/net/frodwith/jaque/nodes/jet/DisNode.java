package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;

// TODO: See +dis get called in practice before removing printfs.
//
@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class),
  @NodeChild(value="b", type=SlotNode.class)
})
public abstract class DisNode extends SubjectNode {
  @Specialization
  protected long dis(long a, long b) throws ArithmeticException {
    System.err.println("dis");
    return HoonMath.dis(a, b);
  }

  @Fallback
  protected Object dis(Object a, Object b) {
    System.err.println("dis");
    return HoonMath.dis(needAtom(a), needAtom(b));
  }
}
