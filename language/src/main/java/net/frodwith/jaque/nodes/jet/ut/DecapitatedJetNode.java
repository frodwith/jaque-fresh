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

@NodeField(name="localContext", type=AstContext.class)
public abstract class DecapitatedJetNode extends SubjectNode {
  protected abstract AstContext getLocalContext();

  protected Object lookupOrExecute(long cacheId, Cell cacheKey, Object cor)
      throws ExitException
  {
    AstContext astContext = getLocalContext();
    NockContext nockContext = astContext.getNockContext();

    Object cached = nockContext.lookupMemo(cacheId, cacheKey);
    //    System.err.print(null != cached ? "H" : ".");
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
  }
}
