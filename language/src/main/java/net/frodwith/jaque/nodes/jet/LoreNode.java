package net.frodwith.jaque.nodes.jet;

import java.util.Arrays;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;

@NodeChild(value="a", type=SlotExpressionNode.class)
public abstract class LoreNode extends SubjectNode {
  @Specialization
  protected Object lore(Object a) {
    System.err.println("lore");
    try {
      return doLore(a);
    }
    catch ( ExitException e ) {
      throw new NockException(e.getMessage(), this);
    }
  }

  @TruffleBoundary
  public static Object doLore(Object lub) throws ExitException {
    int pos = 0;
    Object tez = 0L;
    byte[] lubytes = Atom.toByteArray(lub);
    int len = lubytes.length;

    while ( true ) {
      int meg = 0;
      boolean end;
      byte byt;
      while ( true ) {
        if ( pos >= len ) {
          byt = 0;
          end = true;
          break;
        }
        byt = pos+meg < len ? lubytes[pos+meg] : 0;

        if ( 10 == byt || 0 == byt ) {
          end = (byt == 0);
          break;
        }
        else {
          ++meg;
        }
      }
      if ( (byt == 0) && ((pos + meg + 1) < len) ) {
        throw new ExitException("bad lore");
      }
      byte[] byts = Arrays.copyOfRange(lubytes, pos, pos+meg);
      if ( pos >= len ) {
        return List.flop(tez);
      }
      else {
        Object mega = Atom.fromByteArray(Arrays.copyOf(byts, meg));
        tez = new Cell(mega, tez);
        if ( end ) {
          return List.flop(tez);
        }
        pos += meg + 1;
      }
    }
  }
}
