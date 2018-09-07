package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

@TypeSystemReference(NockTypes.class)
@NodeInfo(language = "nock")
@GenerateWrapper
@ReportPolymorphism
public abstract class NockExpressionNode extends Node implements InstrumentableNode {
  private final Object axisInFormula;

  protected NockExpressionNode(SourceSection sourceSection, Object axisInFormula) {
    this.sourceSection = sourceSection;
    this.axisInFormula = axisInFormula;
  }

  protected NockExpressionNode(Object axisInFormula) {
    this(null, axisInFormula);
  }

  protected final NockRootNode getNockRootNode() {
    return (NockRootNode) getRootNode();
  }

  public abstract Object executeGeneric(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectLong(executeGeneric(frame));
  }

  public BigAtom executeBigAtom(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.executeBigAtom(executeGeneric(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectCell(executeGeneric(frame));
  }

  @Override
  @TruffleBoundary
  public final SourceSection getSourceSection() {
    return getNockRootNode().getChildSourceSection(axisInFormula);
  }

  @Override
  public WrapperNode createWrapper(ProbeNode probeNode) {
    return new NockExpressionNodeWrapper(this, probeNode);
  }
}
