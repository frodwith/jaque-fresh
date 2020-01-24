package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.NockNode;

public abstract class DeepOpNode extends NockNode {
  public abstract boolean executeDeep(Object a);

  @Specialization
  protected boolean cell(Cell a) {
    return true;
  }

  @Fallback
  protected boolean other(Object o) {
    return false;
  }
}
