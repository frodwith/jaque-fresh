package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonSerial;

@NodeChild(value="sample", type=SlotExpressionNode.class)
public abstract class JamNode extends SubjectNode {
  @Specialization
  protected Object jam(Object sample) {
    return HoonSerial.jam(sample);
  }
}
