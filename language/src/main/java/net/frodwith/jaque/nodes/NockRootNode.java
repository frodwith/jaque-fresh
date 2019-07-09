package net.frodwith.jaque.nodes;


import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import net.frodwith.jaque.NockLanguage;

@NodeInfo(language = "nock")
public final class NockRootNode extends RootNode {
  @Child private NockExpressionNode bodyNode;

  public NockRootNode(NockLanguage language,
                      NockExpressionNode bodyNode) {
    super(language, NockLanguage.DESCRIPTOR);
    this.bodyNode = bodyNode;
  }

  @Override
  public SourceSection getSourceSection() {
    return bodyNode.getSourceSection();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[0]);
    return bodyNode.executeGeneric(frame);
  }
}
