package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.NounsKey;
import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.PullNodeGen;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.IdentityNode;
import net.frodwith.jaque.nodes.NockEvalNode;
import net.frodwith.jaque.nodes.NockHeadCallNode;
import net.frodwith.jaque.nodes.NockFunctionLookupNode;
import net.frodwith.jaque.nodes.NockFunctionLookupNodeGen;
import net.frodwith.jaque.exception.NockException;

public final class DecapitatedNode extends SubjectNode {
  protected final AstContext astContext;
  private @Child SaveNode saveNode;
  private @Child NounsKeyNode keyNode;
  private @Child SubjectNode nockNode;

  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, DecapitatedNode.class);

  public DecapitatedNode(AstContext astContext,
                         Axis armAxis,
                         SaveNode saveNode,
                         NounsKeyNode keyNode) {
    this.astContext = astContext;
    this.keyNode = keyNode;
    this.saveNode = saveNode;

    SlotNode slot = new SlotNode(armAxis);
    NockFunctionLookupNode look = NockFunctionLookupNodeGen.create(slot, astContext);
    NockEvalNode eval = new NockEvalNode(look, new IdentityNode());
    this.nockNode = new NockHeadCallNode(eval);
  }

  public final Object executeGeneric(VirtualFrame frame) {
    NounsKey key;
    Object product;

    try {
      key = keyNode.executeKey(frame);
    }
    catch ( NockException e ) {
      CompilerDirectives.transferToInterpreter();
      LOG.warning(e.getMessage());
      return nockNode.executeGeneric(frame);
    }

    product = astContext.getNockContext().lookupMemo(key);

    if ( null == product ) {
      product = nockNode.executeGeneric(frame);
      saveNode.executeSave(frame, key, product);
    }

    return product;
  }
}
