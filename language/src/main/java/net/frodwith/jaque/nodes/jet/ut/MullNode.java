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
    @NodeChild(value="gol", type=SlotNode.class),
    @NodeChild(value="dox", type=SlotNode.class),
    @NodeChild(value="gen", type=SlotNode.class),
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
})
public abstract class MullNode extends DecapitatedJetNode {
  private final static long C3__MULL = 0x6c6c_756dL;

  @Specialization
  protected Object mull(Object cor,
                        Object gol,
                        Object dox,
                        Object gen,
                        Object vet,  // Pull vet out of what was van.
                        Object sut) {
    try {
      // We have to switch between two different caches based off of the vet
      // flag.
      long cacheId = 141 + C3__MULL + (Atom.requireLong(vet) << 8);
      Cell cacheKey = new Cell(sut, new Cell(gol, new Cell(dox, gen)));
      return lookupOrExecute(cacheId, cacheKey, cor);
    } catch (ExitException e) {
      throw new NockException("failure running real mull", this);
    }
  }
}
