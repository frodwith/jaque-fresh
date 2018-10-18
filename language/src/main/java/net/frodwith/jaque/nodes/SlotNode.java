package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;

@NodeInfo(shortName = "0")
public final class SlotNode extends NockExpressionNode {
  @Child private FragmentNode fragmentNode;

  public SlotNode(Axis axis) {
    this.fragmentNode = FragmentNode.fromAxis(axis);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return fragmentNode.executeFragment(NockLanguage.getSubject(frame));
  }
}
