package net.frodwith.jaque.nodes;

import java.util.ArrayDeque;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Axis;
import net.frodwith.jaque.exception.CellRequiredException;
import net.frodwith.jaque.exception.Bail;

public final class FragmentNode extends Node {
  @Children private final FragmentPartNode[] parts;

  private static abstract class FragmentPartNode extends Node {
    public abstract Object executePart(Cell cell);
  }

  private static final class HeadNode extends FragmentPartNode {
    public Object executePart(Cell cell) {
      return cell.head;
    }
  }

  private static final class TailNode extends FragmentPartNode {
    public Object executePart(Cell cell) {
      return cell.tail;
    }
  }

  private FragmentNode(FragmentPartNode[] parts) {
    this.parts = parts;
  }

  @ExplodeLoop
  public Object executeFragment(Object o) {
    try {
      for ( FragmentPartNode node : parts ) {
        o = node.executePart(Cell.require(o));
      }
    }
    catch ( CellRequiredException e ) {
      throw new Bail("atom fragment", this);
    }

    return o;
  }

  public static FragmentNode fromAxis(Axis a) {
    ArrayDeque<FragmentPartNode> tmp = new ArrayDeque<>();
    for ( Axis.Fragment f : a ) {
      FragmentPartNode node = ( f == Axis.Fragment.HEAD )
        ? new HeadNode()
        : new TailNode();
      tmp.add(node);
    }
    return new FragmentNode(tmp.toArray(new FragmentPartNode[tmp.size()]));
  }
}
