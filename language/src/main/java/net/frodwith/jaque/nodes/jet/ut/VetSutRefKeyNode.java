package net.frodwith.jaque.nodes.jet.ut;

import java.util.Objects;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeField(name="cacheId", type=String.class)
@NodeChildren({
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
    @NodeChild(value="ref", type=SlotNode.class),
})
public abstract class VetSutRefKeyNode extends KeyNode {
  protected abstract String getCacheId();

  @Specialization
  protected Object crop(Object vet,  // Pull vet out of what was van.
                        Object sut,
                        Object ref) // u3x_sam
  {
    try {
      return new VetSutRefKey(getCacheId(), Atom.requireLoobean(vet), sut, ref);
    }
    catch ( ExitException e ) {
      throw new NockException("crop key extraction", this);
    }
  }
}
