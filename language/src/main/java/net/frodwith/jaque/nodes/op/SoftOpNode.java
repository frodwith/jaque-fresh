package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.NeedException;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.MetaException;

import net.frodwith.jaque.nodes.NockNode;

public final class SoftOpNode extends NockNode {
  private final ContextReference<NockContext> contextReference;
  private @Child EvalOpNode evalNode;

  public SoftOpNode(AstContext astContext) {
    this.contextReference = astContext.language.getContextReference();
    this.evalNode = new EvalOpNode(astContext, false);
  }

  public Cell executeSoft(Object subject, Object formula, Object fly) {
    try {
      Object product = contextReference.get().withFly(fly,
        () -> evalNode.executeNock(subject, formula));
      return new Cell(0L, product);
    }
    catch ( NeedException e ) {
      return new Cell(1L, e.getPath());
    }
    catch ( NockException e ) {
      // TODO: have the stack hints set a special frame variable.
      //       use the TruffleStackTrace class to examine the call stack
      return new Cell(2L, 0L);
    }
    catch ( MetaException e ) {
      throw e.cause;
    }
  }
}
