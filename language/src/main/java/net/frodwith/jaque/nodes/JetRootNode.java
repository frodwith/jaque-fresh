package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;

@NodeInfo(language = "nock")
public final class JetRootNode extends RootNode {
  @Child private NockSubjectNode bodyNode;
  
  private static final Source JET_SOURCE
    = Source.newBuilder(NockLanguage.ID, "", "<jet source unavailable>")
    .build();

  public NockRootNode(NockLanguage language,
                      FrameDescriptor frameDescriptor,
                      NockSubjectNode bodyNode) {
    super(language, frameDescriptor);
    this.bodyNode = bodyNode;
  }

  @Override
  public SourceSection getSourceSection() {
    return JET_SOURCE.createUnavailableSection();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[0]);
    return bodyNode.executeGeneric(frame);
  }
}
