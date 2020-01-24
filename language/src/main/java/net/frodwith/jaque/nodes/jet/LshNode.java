package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="a", type=SlotExpressionNode.class),
  @NodeChild(value="b", type=SlotExpressionNode.class),
  @NodeChild(value="c", type=SlotExpressionNode.class)
})
public abstract class LshNode extends SubjectNode {
  @Specialization
  protected Object lsh(Object a, Object b, Object c) {
    try {
      int ai = Atom.requireInt(a);
      int bi = Atom.requireInt(b);
      if (ai < 256) {
        return HoonMath.lsh((byte)ai, bi, c);
      } else {
        throw new NockException("lsh bloq too large", this);
      }
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
