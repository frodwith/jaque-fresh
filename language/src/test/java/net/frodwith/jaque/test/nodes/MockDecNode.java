package net.frodwith.jaque.test.nodes;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="sample", type=SubjectNode.class)
public abstract class MockDecNode extends SubjectNode {
  public static boolean called = false;

  // play dumb - we want to make sure specialization works,
  // but this isn't the real dec node.
  @Specialization
  protected Object dec(Object sample) {
    called = true;
    try {
      return HoonMath.dec(sample);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
