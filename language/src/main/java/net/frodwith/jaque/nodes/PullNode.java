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
                  guards = { "sameCells(cachedCore, core)" },
                  assumptions = "object.getAssumption()")
  protected NockCall doStatic(Cell core,
    @Cached("core") Cell cachedCore,
    @Cached("getObject(core)") NockObject object,
    @Cached("new(getArm(cachedCore, object), cachedCore)") NockCall call) {
    return call;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "fine(core, object)",
                  assumptions = "object.getAssumption()",
                  replaces = "doStatic")
  protected NockCall doFine(Cell core,
    @Cached("getObject(core)") NockObject object,
    @Cached("getArm(core, object)") CallTarget arm) {
    return new NockCall(arm, core);
  }

  protected final NockObject getObject(Cell core) {
    try {
      return core.getMeta().getObject(core, getAstContext());
    }
    catch ( ExitException e ) {
      throw new NockException("object resolution failed", this);
    }
  }

  protected final boolean fine(Cell core, NockObject object) {
    return getAstContext().checkFine(core, object);
  }

  @Specialization(replaces = "doFine")
  protected NockCall doSlow(Cell core) {
    return new NockCall(getArm(core, getObject(core)), core);
  }

  @Fallback
  protected NockCall doAtom(Object atom) {
    throw new NockException("atom not core", this);
  }

  private FragmentNode getFragmentNode() {
    if ( null == fragmentNode ) {
      CompilerDirectives.transferToInterpreter();
      Axis inBattery = getArmAxis().mas();
      fragmentNode = FragmentNode.fromAxis(inBattery);
      insert(fragmentNode);
    }
    return fragmentNode;
  }

  protected CallTarget getArm(Cell core, NockObject object) {
    try {
      return object.getArm(core, getArmAxis(), getFragmentNode());
    }
    catch ( ExitException e ) {
      throw new NockException("fail to fetch arm from battery", e, this);
    }
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
