package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;

@NodeChildren({
  @NodeChild(value="a", type=SlotNode.class),
  @NodeChild(value="b", type=SlotNode.class)
})
public abstract class LteNode extends SubjectNode {
  @Specialization
  protected long lte(long a, long b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }

  @Fallback
  protected Object lte(Object a, Object b) {
    return (Atom.compare(a, b) < 1) ? Atom.YES : Atom.NO;
  }
}
