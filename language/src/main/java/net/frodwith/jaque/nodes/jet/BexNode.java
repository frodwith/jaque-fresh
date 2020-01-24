package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="sample", type=SlotExpressionNode.class)
public abstract class BexNode extends SubjectNode {
  @Specialization
  protected Object bex(long sample) {
    return HoonMath.bex(sample);
  }

  @Fallback
  protected Object bex(Object sample) {
    throw new NockException("unrealistically sized bex", this);
  }
}
