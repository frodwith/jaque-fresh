package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;

@NodeInfo(language = "nock")
public final class NockRootNode extends RootNode {

  public static final class IndexLength {
    public int index;
    public int length;

    public IndexLength(int index, int length) {
      this.index  = index;
      this.length = length;
    }
  }

  @Child private NockExpressionNode bodyNode;
  private SourceSection sourceSection;
  private Map<Object, IndexLength> where;

  public NockRootNode(NockLanguage language,
                      FrameDescriptor frameDescriptor,
                      SourceSection sourceSection) {
    super(language, frameDescriptor);
    this.sourceSection = sourceSection;
  }

  @TruffleBoundary
  private void forceSourceSection() {
    if ( null == sourceSection ) {
      assert( null == where );
      StringBuilder buf = new StringBuilder();
      where = new HashMap<>();
      bodyNode.populateSection(buf, where);
      String content = buf.toString();
      sourceSection = Source.newBuilder("nock", content, "(generated)")
        .internal(true)
        .buildLiteral()
        .createSection(0, content.length());
    }
  }

  public SourceSection getChildSourceSection(Object axis) {
    forceSourceSection();
    IndexLength il = where.get(axis);
    if ( null == il ) {
      return null;
    }
    return sourceSection.getSource().createSection(il.index, il.length);
  }

  @Override
  public SourceSection getSourceSection() {
    forceSourceSection();
    return sourceSection;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return bodyNode.executeGeneric(frame);
  }
}
