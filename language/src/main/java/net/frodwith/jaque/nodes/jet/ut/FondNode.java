package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.Atom;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChildren({
    @NodeChild(value="cor", type=SlotNode.class),
    @NodeChild(value="way", type=SlotNode.class),
    @NodeChild(value="hyp", type=SlotNode.class),
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
})
@NodeField(name="localContext", type=AstContext.class)
public abstract class FondNode extends SubjectNode {
   protected abstract AstContext getLocalContext();

  @Specialization
  protected Object fond(VirtualFrame frame,
                        Object cor,
                        Object way,
                        Object hyp,
                        Object vet,  // Pull vet out of what was van.
                        Object sut) {
    AstContext astContext = getLocalContext();
    NockContext nockContext = astContext.getNockContext();

    try {
      // We have to switch between two different caches based off of the vet
      // flag.
      long cacheId = 141 + 0x646e_6f66L + (Atom.requireLong(vet) << 8);

      Cell cacheKey = new Cell(sut, new Cell(way, hyp));
      Object cached = nockContext.lookupMemo(cacheId, cacheKey);
      if (null != cached) {
        return cached;
      }

      Object subject = cor;
      Cell formula = Cell.require(Axis.HEAD.fragment(subject));
      CallTarget fn = formula.getMeta()
                      .getFunction(formula, astContext).callTarget;
      NockCall call = new NockCall(fn, subject);

      NockCallDispatchNode dispatch = NockCallDispatchNodeGen.create();

      Object retVal = dispatch.executeCall(call);
      nockContext.recordMemo(cacheId, cacheKey, retVal);
      return retVal;
    } catch (ExitException e) {
      throw new NockException("failure running real fond", this);
    }
  }
}
