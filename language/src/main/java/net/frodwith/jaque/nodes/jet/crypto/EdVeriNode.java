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

    try {
      byte[] signature = Atom.forceBytes(s, 64);
      byte[] message = Atom.toByteArray(m);
      byte[] publicKey = Atom.forceBytes(pk, 32);

      int ret = Ed25519.ed25519_verify(signature, message, publicKey);
      return (ret == 1) ? Atom.YES : Atom.NO;
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
