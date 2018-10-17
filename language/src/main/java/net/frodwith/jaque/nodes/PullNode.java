package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.runtime.Axis;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.Equality;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
@NodeFields({
  @NodeField(name="armAxis", type=Object.class),
  @NodeField(name="contextReference", type=ContextReference.class),
})
public abstract class PullNode extends NockCallLookupNode {
  public static final int INLINE_CACHE_SIZE = 2;
  @Child private FragmentNode fragmentNode;
  
  public abstract Object getArmAxis();
  protected abstract ContextReference<NockContext> getContextReference();

  @Specialization(limit = "1",
                  guards = "sameCells(object.cell, core)",
                  assumptions = "object.valid")
  protected NockCall doStatic(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("new(getArm(object), object.cell)") NockCall call) {
    return call;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "check(fine, core)",
                  assumptions = "object.valid",
                  replaces = "doStatic")
  protected NockCall doFine(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("getArm(object)") NockFunction arm,
    @Cached("object.createFine()") NockObject.Fine fine) {
    return new NockCall(arm, core);
  }

  @Specialization(replaces = "doFine")
  protected NockCall doSlow(Cell core) {
    NockObject obj = getObject(core);
    NockFunction arm = getArm(obj);
    return new NockCall(arm, core);
  }

  @Fallback
  protected NockCall doAtom(Object atom) {
    throw new Bail("atom not core", this);
  }

  private FragmentNode getFragmentNode() {
    if ( null == fragmentNode ) {
      CompilerDirectives.transferToInterpreter();
      fragmentNode = FragmentNode.fromAxis(new Axis(getArmAxis()));
      insert(fragmentNode);
    }
    return fragmentNode;
  }

  protected NockFunction getArm(NockObject object) {
    Object axis = getArmAxis();
    NockFunctionRegistry registry = getContextReference().get().functionRegistry;
    FragmentNode frag = getFragmentNode();
    try {
      return object.getArm(axis, registry, frag);
    }
    catch ( Fail e ) {
      throw new Bail("fail to fetch arm from battery", this);
    }
  }

  protected NockObject getObject(Cell core) {
    return core.getMeta().getObject(getContextReference().get().dashboard);
  }

  protected static boolean check(NockObject.Fine fine, Cell core) {
    return fine.check(core);
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
