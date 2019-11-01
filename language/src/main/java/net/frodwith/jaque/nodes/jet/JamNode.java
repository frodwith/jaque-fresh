package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonSerial;

@NodeChild(value="sample", type=SlotNode.class)
public abstract class JamNode extends SubjectNode {
  @Specialization
  protected Object jam(Object sample) {
    System.err.println("jam");
    return HoonSerial.jam(sample);
  }
}
