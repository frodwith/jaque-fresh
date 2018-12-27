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
public abstract class AddNode extends SubjectNode {
  // overflow is rare - most times we can return a long
  @Specialization(rewriteOn = ArithmeticException.class)
  protected long longLongs(long a, long b) throws ArithmeticException {
    return HoonMath.addLongs(a, b);
  }

  @Specialization(replaces="longLongs")
  protected Object genLongs(long a, long b) {
    return HoonMath.add(a, b);
  }

  @Fallback
  protected Object add(Object a, Object b) {
    return HoonMath.add(needAtom(a), needAtom(b));
  }
}
