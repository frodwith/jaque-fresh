package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;

@NodeInfo(shortName = "0")
public final class SlotNode extends NockExpressionNode {
  @Child private FragmentNode fragmentNode;

  private SlotNode(FragmentNode fragmentNode) {
    this.fragmentNode = fragmentNode;
  }

  public static SlotNode fromPath(Iterable<Boolean> path) {
    return new SlotNode(FragmentNode.fromPath(path));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return fragmentNode.executeFragment(NockLanguage.getSubject(frame));
  }
}
