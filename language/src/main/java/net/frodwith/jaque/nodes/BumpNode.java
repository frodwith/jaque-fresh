package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.runtime.HoonMath;

public abstract class BumpNode extends UnaryNode {
  @Specialization
  protected Object doCell(Cell cell) {
    throw new NockException("cell required", this);
  }

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long doLong(long atom) {
    return HoonMath.unsignedIncrementExact(atom);
  }

  @Fallback
  protected Object doAtom(Object atom) {
    return HoonMath.increment(atom);
  }
}
