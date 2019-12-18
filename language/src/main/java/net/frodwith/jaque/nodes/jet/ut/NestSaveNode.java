package net.frodwith.jaque.nodes.jet.ut;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.nodes.SubjectNode;

public final class NestSaveNode extends SaveNode {
  private @Child SubjectNode segNode;
  private @Child SubjectNode regNode;

  public NestSaveNode(AstContext astContext,
                      SubjectNode segNode,
                      SubjectNode regNode) {
    super(astContext);
    this.segNode = segNode;
    this.regNode = regNode;
  }

  private void save(Object key, Object product) {
    astContext.getNockContext().newRecordMemo(key, product);
  }

  public void executeSave(VirtualFrame frame, Object key, Object product) {
    if ( product instanceof Long ) {
      switch ( (int) (long) product ) {
        case 0: {
          Object reg = regNode.executeGeneric(frame);
          if ( reg instanceof Long && 0L == (long) reg ) {
            save(key, product);
          }
          break;
        }
        case 1: {
          Object seg = segNode.executeGeneric(frame);
          if ( seg instanceof Long && 0L == (long) seg ) {
            save(key, product);
          }
          break;
        }
      }
    }
  }
}
