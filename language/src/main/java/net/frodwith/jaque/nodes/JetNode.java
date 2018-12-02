package net.frodwith.jaque.nodes;

public abstract class JetNode extends NockNode {
  private static final Source JET_SOURCE
    = Source.newBuilder(NockLanguage.ID, "", "nock jet").build();

  public RootCallTarget createCallTarget(NockLanguage language) {
  }

  private static final class JetRootNode extends RootNode {
    public JetRootNode(NockLanguage language, NockExpressionNode body) {
      super(language, NockLanguage.DESCRIPTOR);
    }
    
    @Override
    public SourceSection getSourceSection() {
      return JET_SOURCE.createUnavailableSection();
    }
  }
}
