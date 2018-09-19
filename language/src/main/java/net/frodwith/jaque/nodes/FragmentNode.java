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

public final class FragmentNode extends NockExpressionNode {
  @Children private final FragmentPartNode[] parts;

  private static abstract class FragmentPartNode extends Node {
    public abstract Object executePart(VirtualFrame frame, Object o) throws CellRequiredException;
  }

  private static final class HeadNode extends FragmentPartNode {
    public Object executePart(VirtualFrame frame, Object o) throws CellRequiredException {
      return Cell.require(o).head;
    }
  }

  private static final class TailNode extends FragmentPartNode {
    public Object executePart(VirtualFrame frame, Object o) throws CellRequiredException {
      return Cell.require(o).tail;
    }
  }

  private FragmentNode(FragmentPartNode[] parts) {
    this.parts = parts;
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object o = NockLanguage.getSubject(frame);

    try {
      for ( FragmentPartNode node : parts ) {
        o = node.executePart(frame, o);
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
