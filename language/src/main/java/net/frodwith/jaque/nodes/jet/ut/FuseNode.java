package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChildren({
    @NodeChild(value="cor", type=SlotNode.class),
    @NodeChild(value="ref", type=SlotNode.class),
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
})
public abstract class FuseNode extends DecapitatedJetNode {
  private final static long C3__FUSE = 0x6573_7566L;

  @Specialization
  protected Object fuse(Object cor,
                        Object ref,   // u3x_sam
                        Object vet,  // Pull vet out of what was van.
                        Object sut) {
    try {
      // We have to switch between two different caches based off of the vet
      // flag.
      long cacheId = 141 + C3__FUSE + (Atom.requireLong(vet) << 8);
      Cell cacheKey = new Cell(sut, ref);
      return lookupOrExecute(cacheId, cacheKey, cor);
    } catch (ExitException e) {
      throw new NockException("failure running real fuse", this);
    }
  }
}
