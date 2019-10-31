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
public abstract class DivNode extends SubjectNode {
  @Specialization
  protected Object genLongs(long a, long b) {
    try {
      return HoonMath.div(a, b);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }

  @Fallback
  protected Object div(Object a, Object b) {
    try {
      return HoonMath.div(needAtom(a), needAtom(b));
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
