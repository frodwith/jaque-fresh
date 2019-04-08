package net.frodwith.jaque.nodes;

import java.util.function.Supplier;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
@NodeFields({
  @NodeField(name="armAxis", type=Axis.class),
  @NodeField(name="astContext", type=AstContext.class),
})
public abstract class PullNode extends NockCallLookupNode {
  public static final int INLINE_CACHE_SIZE = 2;
  @Child private FragmentNode fragmentNode;
  
  public abstract Axis getArmAxis();
  protected abstract AstContext getAstContext();

  @Specialization(limit = "1",
                  guards = "sameCells(cachedCore, core)",
                  assumptions = "klass.valid")
  protected NockCall doStatic(Cell core,
    @Cached("core") Cell cachedCore,
    @Cached("getNockClass(core)") NockClass klass,
    @Cached("new(getArm(cachedCore), cachedCore)") NockCall call) {
    return call;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "fine(core, klass)",
                  assumptions = "klass.valid",
                  replaces = "doStatic")
  protected NockCall doFine(Cell core,
    @Cached("getNockClass(core)") NockClass klass,
    @Cached("getArm(core)") CallTarget arm) {
    return new NockCall(arm, core);
  }

  @Specialization(replaces = "doFine")
  protected NockCall doSlow(Cell core) {
    return new NockCall(getArm(core), core);
  }

  @Fallback
  protected NockCall doAtom(Object atom) {
    throw new NockException("atom not core", this);
  }

  protected final NockClass getNockClass(Cell core) {
    try {
      return core.getMeta().getNockClass(core, getAstContext());
    }
    catch ( ExitException e ) {
      throw new NockException("object resolution failed", this);
    }
  }

  protected final boolean fine(Cell core, NockClass klass) {
    return klass.getFine(core).check(core, getAstContext().dashboard);
  }

  private FragmentNode getFragmentNode() {
    if ( null == fragmentNode ) {
      CompilerDirectives.transferToInterpreter();
      fragmentNode = FragmentNode.fromAxis(getArmAxis());
      insert(fragmentNode);
    }
    return fragmentNode;
  }

  protected final CallTarget getArm(Cell core) {
    try {
      return core.getMeta().getArm(core, getArmAxis(), getFragmentNode());
    }
    catch ( ExitException e ) {
      throw new NockException("fail to fetch arm from battery", e, this);
    }
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
