package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.NeedException;
import net.frodwith.jaque.exception.MetaException;
import net.frodwith.jaque.nodes.NockNode;

public final class EscapeOpNode extends NockNode {
  private @Child SlamOpNode slamNode;

  public EscapeOpNode(AstContext astContext) {
    this.slamNode = new SlamOpNode(astContext, false);
  }

  public Object executeEscape(Object fly, Object ref, Object gof) {
    Object product;

    assert( null != fly );
    try {
      product = slamNode.executeSlam(fly, new Cell(ref, gof));
    }
    catch ( NockException e ) {
      throw new MetaException(e);
    }

    if ( product instanceof Cell ) {
      Object u = ((Cell) product).tail;
      if ( u instanceof Cell ) {
        return ((Cell) u).tail;
      }
      else {
        // from this exception
        // TODO: add hunk of mush(gof) to the call stack
        throw new NockException("hunk", this);
      }
    }
    else {
      throw new NeedException(gof);
    }
  }
}
