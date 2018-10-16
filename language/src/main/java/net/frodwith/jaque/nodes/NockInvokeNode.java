package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.Axis;
import net.frodwith.jaque.data.NockObject;

public abstract class NockInvokeNode extends NockExpressionNode {
  private final Object armAxis;
  private final ContextReference<NockContext> contextReference;
  @Child private NockObjectLookupNode lookupNode;
  @Child private FragmentNode fragmentNode;

  protected abstract Object dispatch(NockFunction function, Object subject);

  protected NockInvokeNode(Object armAxis, NockObjectLookupNode lookupNode,
      ContextReference<NockContext> contextReference) {
    this.armAxis = armAxis;
    this.lookupNode = lookupNode;
    this.contextReference = contextReference;
    this.fragmentNode = FragmentNode.fromAxis(new Axis(armAxis));
  }

  public final Object executeGeneric(VirtualFrame frame) {
    NockObject obj = lookupNode.executeLookup(frame);
    NockFunction f;
    try {
      f = obj.getArm(armAxis,
          contextReference.get().functionRegistry, fragmentNode);
    }
    catch ( Fail e ) {
      throw new Bail("arm lookup failed", this);
    }
    return dispatch(f, obj.cell);
  }
}
