package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.AtomAes;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
  @NodeChild(value="key", type=SlotExpressionNode.class),
  @NodeChild(value="iv", type=SlotExpressionNode.class),
  @NodeChild(value="msg", type=SlotExpressionNode.class),
})
@NodeFields({
  @NodeField(name="mode", type=Integer.class),
  @NodeField(name="keySize", type=Integer.class),
})
public abstract class AesCbcNode extends SubjectNode {
  protected abstract int getMode();
  protected abstract int getKeySize();

  @Specialization
  protected Object aesCbc(Object key, Object iv, Object msg)
  {
    System.err.println("aesCbc");

    try {
      return AtomAes.aes_cbc(getMode(), getKeySize(), key, iv, msg);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
