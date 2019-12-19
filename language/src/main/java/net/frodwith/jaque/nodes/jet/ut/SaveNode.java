package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.AstContext;

public abstract class SaveNode extends NockNode {
  protected final AstContext astContext;

  protected SaveNode(AstContext astContext) {
    this.astContext = astContext;
  }

  protected void save(Object key, Object product) {
    astContext.getNockContext().recordMemo(key, product);
  }

  public abstract void executeSave(VirtualFrame frame, Object key, Object product);
}
