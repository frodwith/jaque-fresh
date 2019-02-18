package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.frame.FrameDescriptor;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.exception.ExitException;

public final class TrapRootNode extends NockRootNode {
  private @Child NockExpressionNode bodyNode;
  private final Object trap;

  protected TrapRootNode(NockLanguage language,
                         NockExpressionNode bodyNode,
                         Object trap) {
    super(language);
    this.trap = trap;
    this.bodyNode = bodyNode;
  }

  @Override
  public SourceMappedNoun getSourceMappedNoun() throws ExitException {
    Cell formula = Cell.require(Cell.require(trap).head);
    return SourceMappedNoun.fromCell(formula);
  }

  @Override
  public Object execute(VirtualFrame frame) {
    NockLanguage.setSubject(frame, trap);
    return bodyNode.executeGeneric(frame);
  }

  public static TrapRootNode create(FormulaParser parser, Object trap)
    throws ExitException {
    Cell battery = Cell.require(Cell.require(trap).head);
    return new TrapRootNode(parser.language, parser.parse(battery), trap);
  }
}
