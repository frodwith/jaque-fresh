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
  @NodeChild(value="atom", type=SlotNode.class)
})
public abstract class ShanNode extends SubjectNode {
  @Specialization
  protected Object shan(Object atom) {
    try {
      System.err.println("shan");
      return HoonMath.shan(atom);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
