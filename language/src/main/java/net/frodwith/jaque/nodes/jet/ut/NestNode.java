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
    @NodeChild(value="seg", type=SlotNode.class),
    @NodeChild(value="reg", type=SlotNode.class),
    @NodeChild(value="ref", type=SlotNode.class),
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
})
public abstract class NestNode extends DecapitatedJetNode {
  private final static long C3__NEST = 0x7473_656eL;

  @Specialization
  protected Object nestDext(Object cor,
                            Object seg,
                            Object reg,
                            Object ref,
                            Object vet,
                            Object sut) {
    try {
      // We have to switch between two different caches based off of the vet
      // flag.
      long cacheId = 141 + C3__NEST + (Atom.requireLong(vet) << 8);
      Cell cacheKey = new Cell(sut, ref);

      // We can't just use lookupOrExecute() because of the more complicated
      // caching semantics in +nest.
      Object pro = cacheLookup(cacheId, cacheKey);
      if (null != pro) {
        return pro;
      }

      pro = runCore(cor);
      boolean answer = Atom.requireLoobean(pro);
      if (((answer == true) && Atom.isZero(reg)) ||
          ((answer == false) && Atom.isZero(seg))) {
        cacheRecord(cacheId, cacheKey, pro);
      }

      return pro;
    } catch (ExitException e) {
      e.printStackTrace();
      throw new NockException("failure running real nest", this);
    }
  }
}
