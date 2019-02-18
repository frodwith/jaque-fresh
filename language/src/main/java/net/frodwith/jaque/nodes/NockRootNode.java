package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.exception.ExitException;

@NodeInfo(language = "nock")
public abstract class NockRootNode extends RootNode {
  private SourceMappedNoun source;
  public abstract SourceMappedNoun getSourceMappedNoun() throws ExitException;

  protected NockRootNode(NockLanguage language) {
    super(language, language.DESCRIPTOR);
    this.source = null;
  }

  private SourceMappedNoun getSource() {
    if ( null == source ) {
      try {
        source = getSourceMappedNoun();
      }
      catch ( ExitException e ) {
      }
    }
    return source;
  }

  // assumed to be present by expression nodes
  public SourceSection getChildSourceSection(Axis axis) {
    SourceMappedNoun src = getSource();
    return null == src ? null : src.lookupAxis(axis);
  }

  @Override
  public SourceSection getSourceSection() {
    SourceMappedNoun src = getSource();
    return null == src ? null : src.sourceSection;
  }

  // execute left abstract
}
