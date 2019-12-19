package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.NockException;

public final class WishNode extends NockExpressionNode {
  private final ContextReference<NockContext> contextReference;

  private @Child NockExpressionNode refNode;
  private @Child NockExpressionNode gofNode;
  private @Child EscapeNode escapeNode;

  public WishNode(AstContext astContext,
                  NockExpressionNode refNode,
                  NockExpressionNode gofNode) {
    this.refNode = refNode;
    this.gofNode = gofNode;
    this.escapeNode = new EscapeNode(astContext);
    this.contextReference = astContext.language.getContextReference();
  }

  public Object executeGeneric(VirtualFrame frame) {
    return contextReference.get().peelFly((fly) -> {
      if ( null == fly ) {
        throw new NockException("top escape", this);
      }
      else {
        Object ref = refNode.executeGeneric(frame);
        Object gof = gofNode.executeGeneric(frame);
        return escapeNode.executeEscape(frame, ref, gof, fly);
      }
    });
  }
}
