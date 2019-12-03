package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

import net.frodwith.jaque.Ed25519;
import net.frodwith.jaque.Ed25519Exception;

@NodeChildren({
  @NodeChild(value="aObj", type=SlotNode.class),
  @NodeChild(value="bObj", type=SlotNode.class)
})
public abstract class EdPointAddNode extends SubjectNode {
  @Specialization
  protected Object pointAdd(Object aObj, Object bObj) {
    System.err.println("+pointAdd:ed:crypto");

    byte[] a = Atom.wordsToByteArrayLen(
        Atom.words(aObj), HoonMath.met((byte) 3, aObj), 32,
        Atom.LITTLE_ENDIAN);
    byte[] b = Atom.wordsToByteArrayLen(
        Atom.words(bObj), HoonMath.met((byte) 3, bObj), 32,
        Atom.LITTLE_ENDIAN);

    byte[] output = new byte[32];

    try {
      Ed25519.point_add(output, a, b);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    }

    return Atom.fromByteArray(output, Atom.LITTLE_ENDIAN);
  }
}
