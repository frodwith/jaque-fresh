package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.NockException;

@GenerateNodeFactory
@NodeChild(value="sample", type=SlotNode.class)
public abstract class DecNode extends JetNode {
  @Override
  public final SubjectNode[] createInputNodes() {
    return new SubjectNode[] { new SlotNode(Axis.SAMPLE) };
  }

  @Specialization
  protected long doLong(long atom) {
    if ( 0 == atom ) {
      CompilerDirectives.transferToInterpreter();
      throw new NockException("decrement underflow", this);
    }
    else {
      return atom - 1;
    }
  }

  @Specialization
  protected Object doBig(BigAtom atom) {
    return HoonMath.dec(atom);
  }

  @Fallback
  protected Object doCrash(Object o) {
    throw new NockException("decrement cell", this);
  }
}
