package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.NockException;

public abstract class BumpOpNode extends NockNode {
  public abstract Object executeBump(Object atom);

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long allDirect(long atom) {
    return HoonMath.unsignedIncrementExact(atom);
  }

  @Specialization
  protected Object overflow(long atom) {
    return HoonMath.increment(atom);
  }

  @Specialization
  protected BigAtom big(BigAtom atom) {
    return HoonMath.increment(atom);
  }

  @Fallback
  protected Object wrong(Object wrong) {
    throw new NockException("atom required", this);
  }
}
