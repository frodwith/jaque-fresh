package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class),
  @NodeChild(value="b", type=SlotNode.class)
})
public abstract class MetNode extends SubjectNode {
  @Specialization
  protected long met(Object a, Object b) {
    try {
      int ai = Atom.requireInt(a);
      if (ai < 256) {
        return HoonMath.met((byte)ai, b);
      } else {
        throw new NockException("met bloq too large", this);
      }
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
