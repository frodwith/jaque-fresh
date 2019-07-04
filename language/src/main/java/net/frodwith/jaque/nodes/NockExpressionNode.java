package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
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

import net.frodwith.jaque.util.Lazy;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.library.NounLibrary;

@GenerateWrapper
public abstract class NockExpressionNode extends SubjectNode implements InstrumentableNode {
  private Lazy<SourceSection> sourceSection;

  public abstract Object executeGeneric(VirtualFrame frame);

  // Called by the creating code, not in a constructor because it messes with
  // GenerateWrapper etc.
  public final void setSourceSection(Lazy<SourceSection> sourceSection) {
    this.sourceSection = sourceSection;
  }

  @Override
  @TruffleBoundary
  public final SourceSection getSourceSection() {
    if ( null == sourceSection ) {
      return null;
    }
    else {
      CompilerAsserts.neverPartOfCompilation();
      return sourceSection.get();
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
