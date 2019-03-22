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

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.FineCheck;
import net.frodwith.jaque.dashboard.NockClass;

import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
@NodeFields({
  @NodeField(name="armAxis", type=Axis.class),
  @NodeField(name="dashboard", type=Dashboard.class),
})
public abstract class PullNode extends NockCallLookupNode {
  public static final int INLINE_CACHE_SIZE = 2;
  @Child private FragmentNode fragmentNode;
  
  public abstract Axis getArmAxis();
  protected abstract Dashboard getDashboard();

  @Specialization(limit = "1",
                  guards = { "sameCells(cachedCore, core)" },
                  assumptions = "klass.valid")
  protected NockCall doStatic(Cell core,
    @Cached("core") Cell cachedCore,
    @Cached("getNockClass(core)") NockClass klass,
    @Cached("new(getArm(klass, cachedCore), cachedCore)") NockCall call) {
    return call;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "fine(klass, core)",
                  assumptions = "klass.valid",
                  replaces = "doStatic")
  protected NockCall doFine(Cell core,
    @Cached("getNockClass(core)") NockClass klass,
    @Cached("getArm(klass, core)") CallTarget arm) {
    return new NockCall(arm, core);
  }

  protected NockClass getNockClass(Cell core) {
    try {
      return core.getMeta().getClass(core, getDashboard());
    }
    catch ( ExitException e ) {
      throw new NockException("class resolution failed", this);
    }
  }

  protected boolean fine(NockClass klass, Cell core) {
    return klass.getFine(core).check(core, getDashboard());
  }

  @Specialization(replaces = "doFine")
  protected NockCall doSlow(Cell core) {
    NockClass klass = getNockClass(core);
    CallTarget arm = getArm(klass, core);
    return new NockCall(arm, core);
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

  protected CallTarget getArm(NockClass klass, Cell core) {
    try {
      return klass.getArm(core, getArmAxis(), getFragmentNode());
    }
    catch ( ExitException e ) {
      throw new NockException("fail to fetch arm from battery", e, this);
    }
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
