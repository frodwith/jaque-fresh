package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.exception.NockException;

// TODO: Verify these get called.
//
@NodeChild(value="sample", type=SlotNode.class)
public abstract class MugNode extends SubjectNode {
  @Specialization
  protected long mug(long l) {
    System.err.println("mug(long)");
    return Murmug.get(l);
  }

  @Specialization
  protected long mug(BigAtom ba) {
    System.err.println("mug(BigAtom)");
    return ba.getMug();
  }

  @Specialization
  protected long mug(Cell c) {
    System.err.println("mug(Cell)");
    return c.mug();
  }

  @Fallback
  protected Object mug(Object sample) {
    System.err.println("mug(fallback)");
    return Murmug.get(sample);
  }
}
