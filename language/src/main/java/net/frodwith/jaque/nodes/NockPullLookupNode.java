package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.Equality;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
@NodeField(name="armAxis", type=Object.class)
public abstract class NockPullLookupNode extends NockLookupNode {
  public abstract NockCall executeLookup(VirtualFrame frame);
  public abstract Object getArmAxis();
  @Child private FragmentNode fragmentNode;

  @Specialization(limit = "1",
                  guards = "sameCells(object.cell, core)",
                  assumptions = "object.valid")
  protected NockCall doStatic(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("new(getArm(cachedObject), cachedObject.cell)") NockCall call) {
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
    return object.getArm(getArmAxis(),
        getContextReference().get().functionRegistry,
        getFragmentNode());
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
