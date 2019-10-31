package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="sample", type=SlotNode.class)
public abstract class CapNode extends SubjectNode {
  @Specialization
  protected long cap(Object sample) {
    try {
      return HoonMath.cap(sample);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
