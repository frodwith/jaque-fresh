package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class),
  @NodeChild(value="b", type=SlotNode.class)
})
public abstract class MulNode extends SubjectNode {
  @Specialization(rewriteOn = ArithmeticException.class)
  protected long mulLongs(long a, long b) throws ArithmeticException {
    System.err.println("mul(l, l)");
    return HoonMath.mulLongs(a, b);
  }

  @Specialization(replaces="mulLongs")
  protected Object genMulLongs(long a, long b) {
    System.err.println("mul(l, l)");
    return HoonMath.mul(a, b);
  }

  @Fallback
  protected Object mul(Object a, Object b) {
    System.err.println("mul(o, o)");
    return HoonMath.mul(a, b);
  }
}
