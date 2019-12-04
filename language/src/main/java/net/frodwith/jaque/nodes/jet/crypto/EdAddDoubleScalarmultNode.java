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
  @NodeChild(value="bObj", type=SlotNode.class),
  @NodeChild(value="cObj", type=SlotNode.class),
  @NodeChild(value="dObj", type=SlotNode.class),
})
public abstract class EdAddDoubleScalarmultNode extends SubjectNode {
  @Specialization
  protected Object addDoubleScalarmult(
      Object aObj, Object bObj, Object cObj, Object dObj)
  {
    System.err.println("+add-double-scalarmult:ed:crypto");

    try {
      byte[] a = Atom.forceBytes(aObj, 32);
      byte[] b = Atom.forceBytes(bObj, 32);
      byte[] c = Atom.forceBytes(cObj, 32);
      byte[] d = Atom.forceBytes(dObj, 32);

      byte[] output = new byte[32];
      Ed25519.add_double_scalarmult(output, a, b, c, d);
      return Atom.takeBytes(output, 32);
    } catch (Ed25519Exception e) {
      throw new NockException(e.getMessage(), this);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
