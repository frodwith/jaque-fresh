package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NounsKey;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.nodes.op.SlotOpNode;
import net.frodwith.jaque.nodes.op.EvalOpNode;
import net.frodwith.jaque.nodes.expression.IdentityExpressionNode;
import net.frodwith.jaque.exception.NockException;

public final class DecapitatedNode extends SubjectNode {
  protected final AstContext astContext;
  private @Child SaveNode saveNode;
  private @Child NounsKeyNode keyNode;
  private @Child EvalOpNode nockNode;
  private @Child SlotOpNode slotNode;

  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, DecapitatedNode.class);

  public DecapitatedNode(AstContext astContext,
                         Axis armAxis,
                         SaveNode saveNode,
                         NounsKeyNode keyNode) {
    this.astContext = astContext;
    this.keyNode = keyNode;
    this.saveNode = saveNode;
    this.slotNode = SlotOpNode.fromAxis(armAxis);
    this.nockNode = new EvalOpNode(astContext, false);
  }

  @TruffleBoundary
  private void printHitOrMiss(boolean hit) {
    // We must not call System.err.print() on the inlineable side of a truffle
    // boundary, since it will try to inline into a lock on the stream io.
    if (hit) {
      System.err.print("H");
    } else {
      System.err.print(".");
    }
  }

  private Object nock(Object core) {
    return nockNode.executeNock(core, slotNode.executeSlot(core));
  }

  public final Object executeGeneric(VirtualFrame frame) {
    NounsKey key;
    Object product, core = NockLanguage.getSubject(frame);

    try {
      key = keyNode.executeKey(frame);
    }
    catch ( NockException e ) {
      CompilerDirectives.transferToInterpreter();
      LOG.warning(e.getMessage());
      return nock(core);
    }

    product = astContext.getNockContext().lookupMemo(key);

    boolean cacheMiss = null == product;
    if ( cacheMiss ) {
      product = nock(core);
      saveNode.executeSave(frame, key, product);
    }

    //printHitOrMiss(!cacheMiss);

    return product;
  }
}
