package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.data.SourceMappedNoun;

public final class CellRootNode extends NockRootNode {
  @Child private NockExpressionNode bodyNode;
  private final Object formula;

  protected CellRootNode(NockLanguage language,
                         Object formula,
                         NockExpressionNode bodyNode) {
    super(language);
    this.bodyNode = bodyNode;
    this.formula = formula;
  }

  @Override
  public SourceMappedNoun getSourceMappedNoun() throws ExitException {
    return SourceMappedNoun.fromCell(Cell.require(formula));
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, frame.getArguments()[0]);
    return bodyNode.executeGeneric(frame);
  }

  public static CellRootNode create(FormulaParser parser, Object formula)
    throws ExitException {
    return new CellRootNode(parser.language, formula, parser.parse(formula));
  }
}
