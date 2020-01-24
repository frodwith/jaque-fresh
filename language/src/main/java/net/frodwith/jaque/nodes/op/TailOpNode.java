package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.NockException;

public abstract class TailOpNode extends PartNode {
  @Specialization
  public Object good(Cell cell) {
    return cell.tail;
  }

  @Fallback
  public Object bad(Object bad) {
    throw new NockException("cell required", this);
  }
}
