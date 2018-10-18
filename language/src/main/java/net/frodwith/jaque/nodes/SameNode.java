package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.NodeChild;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Equality;

@NodeChild(value = "left", type = NockExpressionNode.class)
@NodeChild(value = "right", type = NockExpressionNode.class)
public abstract class SameNode extends NockExpressionNode {
  @Specialization
  protected long longs(long a, long b) {
    return a == b ? 0L : 1L;
  }
  
  @Specialization
  protected long longBig(long a, BigAtom b) {
    return 1L;
  }

  @Specialization
  protected long longCell(long a, Cell b) {
    return 1L;
  }

  @Specialization
  protected long bigs(BigAtom a, BigAtom b) {
    return Equality.equals(a, b) ? 0L : 1L;
  }

  @Specialization
  protected long bigLong(BigAtom a, long b) {
    return 1L;
  }

  @Specialization
  protected long bigCell(BigAtom a, Cell b) {
    return 1L;
  }

  @Specialization
  protected long cells(Cell a, Cell b) {
    return Equality.equals(a, b) ? 0L : 1L;
  }

  @Specialization
  protected long cellLong(Cell a, long b) {
    return 1L;
  }

  @Specialization
  protected long cellBig(Cell a, BigAtom b) {
    return 1L;
  }
}
