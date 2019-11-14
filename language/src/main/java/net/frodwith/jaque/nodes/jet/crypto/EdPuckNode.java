package net.frodwith.jaque.nodes.jet.crypto;

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
  @NodeChild(value="a", type=SlotNode.class)
})
public abstract class EdPuckNode extends SubjectNode {
  @Specialization
  protected Object puck(Object a) {
    System.err.println("puck");
    System.exit(-1);
    return null;

    // try {
    //   return HoonMath.mod(a, b);
    // }
    // catch ( ExitException e ) {
    //   throw new NockException(e.getMessage(), this);
    // }
  }
}
