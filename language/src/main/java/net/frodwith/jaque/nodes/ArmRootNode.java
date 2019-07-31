package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.rename.Battery;

@NodeInfo(language = "nock")
public final class ArmRootNode extends RootNode {
/*
  private final Battery battery;
  private final Iterable<Boolean> axis;
  @Child private NockExpressionNode bodyNode;

  @CompilationFinal private Assumption assumption;
  @Children private FineNode[] fineNodes;
  @Children private SubjectNode[] drivers;

  public ArmRootNode(NockLanguage language,
                     NockExpressionNode bodyNode,
                     Iterable<Boolean> axis,
                     Battery battery) {
    super(language, NockLanguage.DESCRIPTOR);
    this.bodyNode = bodyNode;
    this.axis = axis;
    this.battery = battery;
    this.resolve();
  }

  @TruffleBoundary
  private void resolve() {
    CompilerAsserts.neverPartOfCompilation();
    ArmResolution resolution = battery.resolveArm(axis);
    this.assumption = resolution.assumption;
    this.fineNodes = resolution.fineNodes;
    this.drivers = resolution.drivers;
  }

  @Override
  public SourceSection getSourceSection() {
    return bodyNode.getSourceSection();
  }

  @ExplodeLoop
  private Object dispatch(VirtualFrame frame) {
    Object payload = frame.getArguments()[0];
    for ( int i = drivers.length - 1; i >= 0; --i ) {
      Object subject = fineNodes[i].executeCheck(payload);
      if ( null != subject ) {
        SubjectNode driver = drivers[i];
        if ( null == driver ) {
          driver = bodyNode;
        }
        NockLanguage.setSubject(frame, subject);
        return driver.executeGeneric(frame);
      }
    }
    NockLanguage.setSubject(frame, new Core(battery, payload));
    return bodyNode.executeGeneric(frame);
  }

  @Override
  public Object execute(VirtualFrame frame) {
    try {
      assumption.check();
      return dispatch(frame);
    }
    catch ( InvalidAssumptionException e ) {
      resolve();
      return dispatch(frame);
    } 
  }
  */
}
