package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="sample", type=SlotExpressionNode.class)
public abstract class MugNode extends SubjectNode {
  @Specialization
  protected long mug(Object sample) {
    return Murmug.get(sample);
  }
}
