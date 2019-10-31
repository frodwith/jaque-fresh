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
  @NodeChild(value="c", type=SlotNode.class)
})
public abstract class RshNode extends SubjectNode {
  @Specialization
  protected Object rsh(Object a, Object b, Object c) {
    try {
      int ai = Atom.requireInt(a);
      int bi = Atom.requireInt(b);
      if (ai < 256) {
        // TODO: Verify this is ever called.
        System.err.println("rsh");
        return HoonMath.rsh((byte)ai, bi, c);
      } else {
        throw new NockException("rsh bloq too large", this);
      }
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
