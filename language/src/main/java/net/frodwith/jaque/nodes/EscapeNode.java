package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.NeedException;

public final class EscapeNode extends NockNode {
  private final ContextReference<NockContext> contextReference;
  private @Child SubjectNode callNode;

  public EscapeNode(AstContext astContext) {
    this.contextReference = astContext.language.getContextReference();

    // [9 2 [10 6 0 3] 0 2]
    NockExpressionNode large = new SlotNode(Axis.HEAD);
    NockExpressionNode small = new SlotNode(Axis.TAIL);
    EditPartNode chain =
      new EditHeadNode(new EditTailNode(new EditTermNode(small)));
    this.callNode =
      new NockEditNode(large, chain, Axis.SAMPLE, astContext.dashboard);
  }

  public Object executeEscape(VirtualFrame frame, Object ref, Object gof) {
    return contextReference.get().peelFly((fly) -> {
      Object oldSubject = NockLanguage.getSubject(frame);
      NockLanguage.setSubject(frame, new Cell(fly, new Cell(ref, gof)));
      Object product = callNode.executeGeneric(frame);
      if ( product instanceof Cell ) {
        Object u = ((Cell) product).tail;
        if ( u instanceof Cell ) {
          NockLanguage.setSubject(frame, oldSubject);
          return ((Cell) u).tail;
        }
        else {
          // TODO: add hunk of mush(gof) to the call stack
          throw new NockException("hunk", this);
        }
      }
      else {
        throw new NeedException(gof, this);
      }
    });
  }
}
