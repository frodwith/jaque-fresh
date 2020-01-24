package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

import net.frodwith.jaque.Ed25519;
import net.frodwith.jaque.Ed25519Exception;

@NodeChildren({
  @NodeChild(value="pub", type=SlotExpressionNode.class),
  @NodeChild(value="seed", type=SlotExpressionNode.class)
})
public abstract class EdSharNode extends SubjectNode {
  @Specialization
  protected Object shar(Object pub, Object seedObj) {
    System.err.println("shar:ed:crypto");

    try {
      byte[] seed = Atom.forceBytes(seedObj, 32);
      byte[] otherPublicKey = Atom.forceBytes(pub, 32);

      byte[] selfPublicKey = new byte[32];
      byte[] privateKey = new byte[64];
      byte[] shared = new byte[32];

      Ed25519.ed25519_create_keypair(selfPublicKey, privateKey, seed);
      Ed25519.ed25519_key_exchange(shared, otherPublicKey, privateKey);
      return Atom.takeBytes(shared, 32);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
