package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.NounsKey;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;

public final class MemoNode extends NockExpressionNode {
  @CompilationFinal private NounsKey oneKey;
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
    this.oneKey = null;
    this.oneProduct = null;
  }

  public Object executeGeneric(VirtualFrame frame) {
    tossNode.executeGeneric(frame);
    Object subject = NockLanguage.getSubject(frame);
    NockContext context = contextReference.get();

    Object [] keyNouns = new Object[2 + context.flyCount()];
    keyNouns[0] = subject;
    keyNouns[1] = formula;
    int i = 2;
    for ( Object gate : context.flyGates() ) {
      keyNouns[i++] = gate;
    }
    NounsKey key = new NounsKey("nock", keyNouns);

    if ( null == oneKey ) {
      // first run
      CompilerDirectives.transferToInterpreterAndInvalidate();
      oneKey = key;
      oneProduct = valueNode.executeGeneric(frame);
      return oneProduct;
    }
    else if ( null != oneProduct ) {
      // single item cache ( memoized thunks reduced to a few equality checks )
      if ( oneKey.equals(key) ) {
        return oneProduct;
      }
      else {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        oneProduct = null;
      }
    }

    Object product = context.lookupMemo(key);

    if ( null == product ) {
      product = valueNode.executeGeneric(frame);
      context.recordMemo(key, product);
    }

    return product;
  }
}
