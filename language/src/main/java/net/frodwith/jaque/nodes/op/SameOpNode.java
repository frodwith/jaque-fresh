package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.runtime.Equality;

public abstract class SameOpNode extends NockNode {
  public abstract boolean executeSame(Object a, Object b);

  @Specialization
  protected boolean longs(long a, long b) {
    return a == b;
  }

  @Specialization
  protected boolean bigs(BigAtom a, BigAtom b) {
    return Equality.equals(a, b);
  }

  @Specialization
  protected boolean cells(Cell a, Cell b) {
    return Equality.equals(a, b);
  }

  @Fallback
  protected boolean other(Object a, Object b) {
    // Assumption: longs and bigatoms are never equal because a BigAtom is never
    // used to represent a long
    return false;
  }
}
