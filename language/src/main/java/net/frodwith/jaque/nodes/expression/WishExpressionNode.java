package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.nodes.op.EscapeOpNode;

public final class WishExpressionNode extends NockExpressionNode {
  private final ContextReference<NockContext> contextReference;

  private @Child NockExpressionNode refNode;
  private @Child NockExpressionNode gofNode;
  private @Child EscapeOpNode escapeNode;

  public WishExpressionNode(AstContext astContext,
                  NockExpressionNode refNode,
                  NockExpressionNode gofNode) {
    this.refNode = refNode;
    this.gofNode = gofNode;
    this.escapeNode = new EscapeOpNode(astContext);
    this.contextReference = astContext.language.getContextReference();
  }

  public Object executeGeneric(VirtualFrame frame) {
    return contextReference.get().peelFly((fly) -> {
      // the check is in the expression so we don't compute the args
      // for a toplevel escape (which just crashes, not a real operator)
      if ( null == fly ) {
        throw new NockException("top escape", this);
      }
      else {
        Object ref = refNode.executeGeneric(frame);
        Object gof = gofNode.executeGeneric(frame);
        return escapeNode.executeEscape(fly, ref, gof);
      }
    });
  }
}
