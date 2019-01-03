package net.frodwith.jaque.nodes;

import java.util.logging.Level;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Tanks;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.exception.ExitException;

public final class SlogNode extends NockExpressionNode {
  @Child private NockExpressionNode clueNode;
  @Child private NockExpressionNode valueNode;

  private static final TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, SlogNode.class);

  public SlogNode(NockExpressionNode clueNode, NockExpressionNode valueNode) {
    this.clueNode = clueNode;
    this.valueNode = valueNode;
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      Level level;
      Cell clue = clueNode.executeCell(frame);
      StringBuilder buf = new StringBuilder();
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
      Object wall = Tanks.wash(0L, 80L, clue.tail);
      Lists.Iterator i = new Lists.Iterator(wall);
      while ( i.hasNext() ) {
        buf.append(Tapes.toString(i.next()));
        buf.append("\n");
      }
      LOG.log(level, buf.toString());
    }
    catch (ExitException | UnexpectedResultException e) {
    }
    return valueNode.executeGeneric(frame);
  }
}
