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

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

@GenerateWrapper
public abstract class NockExpressionNode extends NockNode implements InstrumentableNode {
  private Axis axisInFormula;

  // Called by the creating code, not in a constructor because it messes with
  // GenerateWrapper etc.
  public final void setAxisInFormula(Axis axis) {
    this.axisInFormula = axis;
  }

  protected final NockRootNode getNockRootNode() {
    return (NockRootNode) getRootNode();
  }

  public abstract Object executeGeneric(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectLong(executeGeneric(frame));
  }

  public BigAtom executeBigAtom(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectBigAtom(executeGeneric(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectCell(executeGeneric(frame));
  }

  @Override
  @TruffleBoundary
  public final SourceSection getSourceSection() {
    if ( null == axisInFormula ) {
      return null;
    }
    else {
      return getNockRootNode().getChildSourceSection(axisInFormula);
    }
  }

  @Override
  public WrapperNode createWrapper(ProbeNode probeNode) {
    return new NockExpressionNodeWrapper(this, probeNode);
  }

  @Override
  public final boolean isInstrumentable() {
    return true;
  }
}
