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
  @NodeChild(value="s", type=SlotNode.class),
  @NodeChild(value="m", type=SlotNode.class),
  @NodeChild(value="pk", type=SlotNode.class)
})
public abstract class EdVeriNode extends SubjectNode {
  @Specialization
  protected long veri(Object s, Object m, Object pk) {
    System.err.println("veri:ed:crypto");

    byte[] signature = Atom.wordsToByteArrayLen(
         Atom.words(s), HoonMath.met((byte) 3, s), 64, Atom.LITTLE_ENDIAN);
    byte[] message = Atom.wordsToBytes(
         Atom.words(m), HoonMath.met((byte) 3, m), Atom.LITTLE_ENDIAN);
    byte[] publicKey = Atom.wordsToByteArrayLen(
         Atom.words(pk), HoonMath.met((byte) 3, pk), 32, Atom.LITTLE_ENDIAN);

    int ret = 0;
    try {
      ret = Ed25519.ed25519_verify(signature, message, publicKey);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    }

    return (ret == 1) ? Atom.YES : Atom.NO;
  }
}
