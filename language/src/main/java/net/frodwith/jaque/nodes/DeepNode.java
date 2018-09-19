package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;

import net.frodwith.jaque.data.Cell;

@NodeChild(value = "value", type = NockExpressionNode.class)
public abstract class DeepNode extends UnaryNode {
  @Specialization
  protected long doCell(Cell cell) {
    return 0L;
  }

  @Fallback
  protected long doAtom(Object o) {
    return 1L;
  }
}
