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
  @NodeChild(value="a", type=SlotNode.class)
})
public abstract class EdPuckNode extends SubjectNode {
  @Specialization
  protected Object puck(Object s) {
    byte[] seed = Atom.wordsToByteArrayLen(
        Atom.words(s), HoonMath.met((byte) 3, s), 32, Atom.LITTLE_ENDIAN);

    System.err.println("puck");

    byte[] publicKey = new byte[32];
    byte[] privateKey = new byte[64];

    try {
      Ed25519.ed25519_create_keypair(publicKey, privateKey, seed);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    }

    System.err.println("-> \"" + publicKey + "\"");
    
    return Atom.fromByteArray(publicKey, Atom.LITTLE_ENDIAN);
  }
}
