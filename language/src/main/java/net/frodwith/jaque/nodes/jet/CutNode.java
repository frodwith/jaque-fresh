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
  @NodeChild(value="b", type=SlotNode.class),
  @NodeChild(value="c", type=SlotNode.class),
  @NodeChild(value="d", type=SlotNode.class)
})
public abstract class CutNode extends SubjectNode {
  @Specialization
  protected Object cut(Object a, Object b, Object c, Object d) {
    try {
      int ai = Atom.requireInt(a);
      if (ai < 256) {
        // TODO: This never gets called, but that might be because we're doing
        // too much work elsewhere.
        System.err.println("cut");
        return HoonMath.cut((byte)ai, b, c, d);
      } else {
        throw new NockException("cut bloq too large", this);
      }
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
