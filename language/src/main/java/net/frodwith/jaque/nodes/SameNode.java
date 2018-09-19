package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Equality;

public abstract class SameNode extends UnaryNode {
  @Specialization
  protected long doSame(Cell a) {
    return Equality.equals(a.head, a.tail) ? 0L : 1L;
  }
}
