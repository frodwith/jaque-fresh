package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Cell;

@NodeChildren({
  @NodeChild(value = "head", type = NockExpressionNode.class),
  @NodeChild(value = "tail", type = NockExpressionNode.class)
})
public abstract class ConsExpressionNode extends NockExpressionNode {
  @Specialization
  public Cell doCons(Object head, Object tail) {
    return new Cell(head, tail);
  }
}
