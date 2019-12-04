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
public abstract class EdScalarmultNode extends SubjectNode {
  @Specialization
  protected Object scalarmult(Object aObj, Object bObj) {
    System.err.println("+scalarmult:ed:crypto");

    try {
      byte[] a = Atom.forceBytes(aObj, 32);
      byte[] b = Atom.forceBytes(bObj, 32);

      byte[] output = new byte[32];
      Ed25519.scalarmult(output, a, b);
      return Atom.takeBytes(output, 32);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
