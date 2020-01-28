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
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;

public final class EscapeNode extends NockNode {
  private @Child NockExpressionNode slamNode;

  public EscapeNode(AstContext astContext) {
    this.slamNode = createSlamNode(astContext);
  }

  private static NockExpressionNode createSlamNode(AstContext c) {
    // [9 2 10 [6 0 3] 0 2], or, slam the head of the subject with the tail
    // TODO FIXME etc. (mainly, etc): Use the operation nodes directly, after
    // making edit ops nodes.
    NockExpressionNode large = new SlotExpressionNode(Axis.HEAD);
    NockExpressionNode small = new SlotExpressionNode(Axis.TAIL);
    EditPartNode chain =
      new EditHeadNode(new EditTailNode(new EditTermNode(small)));
    NockEditNode editNode =
      new NockEditNode(large, chain, Axis.SAMPLE, c.dashboard);
    NockCallLookupNode pull = PullNodeGen.create(editNode, Axis.HEAD, c);

    return new NockHeadCallNode(pull);
  }

  public Object executeEscape(VirtualFrame frame, Object ref, Object gof, Object fly) {
    Object subject, product, gateAndSample;

    subject = NockLanguage.getSubject(frame);
    gateAndSample = new Cell(fly, new Cell(ref, gof));
    NockLanguage.setSubject(frame, gateAndSample);
    product = slamNode.executeGeneric(frame);
    NockLanguage.setSubject(frame, subject);

    if ( product instanceof Cell ) {
      Object u = ((Cell) product).tail;
      if ( u instanceof Cell ) {
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
  }
}
