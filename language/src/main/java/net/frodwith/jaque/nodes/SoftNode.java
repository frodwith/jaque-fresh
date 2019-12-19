package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;
import net.frodwith.jaque.exception.NeedException;
import net.frodwith.jaque.exception.NockException;

public final class SoftNode extends NockNode {
  private @Child SubjectNode callNode;
  private final ContextReference<NockContext> contextReference;

  public SoftNode(AstContext astContext) {
    this.contextReference = astContext.language.getContextReference();

    // [2 [0 2] 0 3]
    SubjectNode subject = new SlotNode(Axis.HEAD);
    SubjectNode formula = new SlotNode(Axis.TAIL);
    NockFunctionLookupNode lookup =
      NockFunctionLookupNodeGen.create(formula, astContext);
    NockEvalNode eval = new NockEvalNode(lookup, subject);
    this.callNode = new NockHeadCallNode(eval);
  }

  public Cell executeSoft(VirtualFrame frame,
                          Object subject, Object formula, Object fly) {
    Object oldSubject = NockLanguage.getSubject(frame);
    try {
      NockLanguage.setSubject(frame, new Cell(subject, formula));
      Object product = contextReference.get().withFly(fly,
        () -> callNode.executeGeneric(frame));
      return new Cell(0L, product);
    }
    catch (NeedException e) {
      return new Cell(1L, e.getPath());
    }
    catch (NockException e) {
      // TODO: have the stack hints set a special frame variable.
      //       use the TruffleStackTrace class to examine the call stack
      return new Cell(2L, 0L);
    }
    finally {
      NockLanguage.setSubject(frame, oldSubject);
    }
  }
}
