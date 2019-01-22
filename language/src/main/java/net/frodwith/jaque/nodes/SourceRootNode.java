package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.data.SourceMappedNoun;

public final class SourceRootNode extends NockRootNode {
  @Child private NockExpressionNode bodyNode;
  private final SourceMappedNoun source;

  protected SourceRootNode(NockLanguage language,
                           SourceMappedNoun source,
                           NockExpressionNode bodyNode) {
    super(language);
    this.bodyNode = bodyNode;
    this.source = source;
  }

  @Override
  public SourceMappedNoun getSourceMappedNoun() throws ExitException {
    return source;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[0]);
    return bodyNode.executeGeneric(frame);
  }

  public static SourceRootNode 
    create(FormulaParser parser, SourceMappedNoun source) throws ExitException {
    return new SourceRootNode(parser.language, 
      source, parser.parse(source.noun));
  }
}
