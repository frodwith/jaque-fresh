package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.AstContext;

public final class UnconditionalSaveNode extends SaveNode {

  public UnconditionalSaveNode(AstContext astContext) {
    super(astContext);
  }

  public void executeSave(VirtualFrame frame, Object key, Object product) {
    save(key, product);
  }
}
