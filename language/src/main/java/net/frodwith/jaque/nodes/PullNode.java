package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.data.NockFunction;

import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.Equality;

import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.FineCheck;

import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
@NodeFields({
  @NodeField(name="armAxis", type=Axis.class),
  @NodeField(name="contextReference", type=ContextReference.class),
})
public abstract class PullNode extends NockCallLookupNode {
  public static final int INLINE_CACHE_SIZE = 2;
  @Child private FragmentNode fragmentNode;
  
  public abstract Axis getArmAxis();
  protected abstract ContextReference<NockContext> getContextReference();

  @Specialization(limit = "1",
                  guards = "sameCells(object.noun, core)",
                  assumptions = "object.klass.valid")
  protected NockCall doStatic(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("new(getArm(object), object.noun)") NockCall call) {
    return call;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "fine.check(core, getSupplier())",
                  assumptions = "object.klass.valid",
                  replaces = "doStatic")
  protected NockCall doFine(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("getArm(object)") NockFunction arm,
    @Cached("object.getFine(getSupplier())") FineCheck fine) {
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
    throw new NockException("atom not core", this);
  }

  private Supplier<Dashboard> getSupplier() {
    return () -> getContextReference().get().dashboard;
  }

  private FragmentNode getFragmentNode() {
    if ( null == fragmentNode ) {
      CompilerDirectives.transferToInterpreter();
      fragmentNode = FragmentNode.fromAxis(getArmAxis());
      insert(fragmentNode);
    }
    return fragmentNode;
  }

  protected NockFunction getArm(NockObject object) {
    Axis a = getArmAxis();
    FragmentNode frag = getFragmentNode();
    ContextReference<NockContext> ref = getContextReference();
    Supplier<NockFunctionRegistry> supply = () -> ref.get().functionRegistry;
    try {
      return object.klass.getArm(a, frag, supply);
    }
    catch ( ExitException e ) {
      throw new NockException("fail to fetch arm from battery", e, this);
    }
  }

  protected NockObject getObject(Cell core) {
    try {
      return core.getMeta().getObject(getSupplier());
    }
    catch ( ExitException e ) {
      throw new NockException("core not object", e, this);
    }
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
