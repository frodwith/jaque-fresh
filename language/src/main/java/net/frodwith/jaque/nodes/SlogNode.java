package net.frodwith.jaque.nodes;

import java.util.logging.Level;

import com.google.common.io.BaseEncoding;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Tanks;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class SlogNode extends NockExpressionNode {
  @Child private NockExpressionNode clueNode;
  @Child private NockExpressionNode valueNode;
  private final ContextReference<NockContext> contextReference;

  private static final Cell edgTab = new Cell(0L, 80L);
  private static final BaseEncoding base64 = BaseEncoding.base64();

  private static final TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, SlogNode.class);

  public SlogNode(NockExpressionNode clueNode, NockExpressionNode valueNode,
                  ContextReference<NockContext> contextReference) {
    this.clueNode = clueNode;
    this.valueNode = valueNode;
    this.contextReference = contextReference;
  }

  private static String wallToString(Object wall) throws ExitException {
    Lists.Iterator i = new Lists.Iterator(wall);
    StringBuilder buf = new StringBuilder();
    while ( i.hasNext() ) {
      buf.append(Tapes.toString(i.next()));
      buf.append("\n");
    }
    return buf.toString();
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      Level level;
      Cell clue = clueNode.executeCell(frame);
      switch ( Atom.requireInt(clue.head) ) {
        case 1:
          level = Level.WARNING;
          break;

        case 2:
          level = Level.SEVERE;
          break;

        default:
          level = Level.INFO;
          break;
      }
      String msg;
      try {
        msg = wallToString(
          contextReference.get().slamHoon("wash", edgTab, clue.tail));
      }
      catch ( ExitException he ) {
        try {
          msg = wallToString(Tanks.wash(edgTab.head, edgTab.tail, clue.tail));
        }
        catch ( ExitException e ) {
          msg = "unknown tank: " +
            base64.encode(HoonSerial.jamBytes(clue.tail));
        }
      }
      LOG.log(level, msg);
    }
    catch (ExitException | UnexpectedResultException e) {
    }
    return valueNode.executeGeneric(frame);
  }
}
