package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

public final class MemoNode extends NockExpressionNode {
  @CompilationFinal private Object oneSubject; 
  @CompilationFinal private Object oneProduct; 
  @Child private NockExpressionNode tossNode;
  @Child private NockExpressionNode valueNode;
  private final Cell formula;
  private final ContextReference<NockContext> contextReference;

  public MemoNode(ContextReference<NockContext> contextReference, Cell formula, 
                  NockExpressionNode tossNode, NockExpressionNode valueNode) {
    this.contextReference = contextReference;
    this.formula = formula;
    this.tossNode = tossNode;
    this.valueNode = valueNode;
    this.oneSubject = null;
    this.oneProduct = null;
  }

  public Object executeGeneric(VirtualFrame frame) {
    tossNode.executeGeneric(frame);
    Object subject = NockLanguage.getSubject(frame);
    if ( null == oneSubject ) {
      // first run
      CompilerDirectives.transferToInterpreterAndInvalidate();
      oneSubject = subject;
      oneProduct = valueNode.executeGeneric(frame);
      return oneProduct;
    }
    else if ( null != oneProduct ) {
      // single item cache ( memoized thunks reduced to an equality check )
      if ( Equality.equals(oneSubject, subject) ) {
        return oneProduct;
      }
      CompilerDirectives.transferToInterpreterAndInvalidate();
      oneProduct = null;
    }

    NockContext context = contextReference.get();
    Object product = context.lookupMemo(0L, new Cell(subject, formula));
    if ( null == product ) {
      product = valueNode.executeGeneric(frame);
      context.recordMemo(0L, new Cell(subject, formula), product);
    }
    return product;
  }
}
