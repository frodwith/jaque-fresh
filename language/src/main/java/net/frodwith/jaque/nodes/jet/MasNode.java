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

@NodeChild(value="sample", type=SlotNode.class)
public abstract class MasNode extends SubjectNode {
  @Specialization
  protected long mas(long sample) {
    return HoonMath.mas(sample);
  }

  @Fallback
  protected Object mas(Object sample) {
    return HoonMath.mas(sample);
  }
}
