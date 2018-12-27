package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="sample", type=SlotNode.class)
public abstract class DecNode extends SubjectNode {
  @Specialization
  protected long decLong(long sample) {
    if ( 0L == sample ) {
      CompilerDirectives.transferToInterpreter();
      throw new NockException("decrement underflow", this);
    }
    else {
      return sample - 1;
    }
  }

  @Specialization
  protected Object decBigAtom(BigAtom sample) {
    return HoonMath.dec(sample);
  }

  @Fallback
  protected Object decOther(Object sample) {
    throw new NockException("decrement cell", this);
  }
}
