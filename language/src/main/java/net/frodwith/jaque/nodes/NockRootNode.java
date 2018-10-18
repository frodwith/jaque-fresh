package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;

@NodeInfo(language = "nock")
public final class NockRootNode extends RootNode {

  @Child private NockExpressionNode bodyNode;
  private final Supplier<SourceMappedNoun> sourceSupplier;
  private SourceMappedNoun source;

  public NockRootNode(NockLanguage language,
                      FrameDescriptor frameDescriptor,
                      Supplier<SourceMappedNoun> sourceSupplier,
                      NockExpressionNode bodyNode) {
    super(language, frameDescriptor);
    this.sourceSupplier = sourceSupplier;
    this.bodyNode       = bodyNode;
    this.source         = null;
  }

  private SourceMappedNoun getSource() {
    if ( null == source ) {
      source = sourceSupplier.get();
    }
    return source;
  }

  public SourceSection getChildSourceSection(Axis axis) {
    return getSource().lookupAxis(axis);
  }

  @Override
  public SourceSection getSourceSection() {
    return getSource().sourceSection;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[0]);
    return bodyNode.executeGeneric(frame);
  }
}
