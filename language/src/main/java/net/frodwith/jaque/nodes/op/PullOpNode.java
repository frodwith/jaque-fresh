package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.nodes.dispatch.DispatchNode;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

public abstract class PullOpNode extends NockNode {
  private final Axis armAxis;
  private final AstContext astContext;
  private @Child SlotOpNode slotNode;
  private @Child DispatchNode dispatchNode;

  public PullOpNode(AstContext astContext, Axis armAxis, boolean tailPosition) {
    this.armAxis = armAxis;
    this.astContext = astContext;
    this.slotNode = SlotOpNode.fromAxis(armAxis);
    this.dispatchNode = DispatchNode.create(tailPosition);
  }

  public abstract Object executePull(Object core);

  @Specialization(limit = "1",
                  guards = "same(cachedCore, core)",
                  assumptions = "klass.valid")
  protected final Object doMemo(Cell core,
    @Cached("core") Cell cachedCore,
    @Cached("getNockClass(cachedCore)") NockClass klass,
    @Cached("doSlow(cachedCore)") Object product) {
    return product;
  }

  @Specialization(limit = "2",
                  guards = "fine(core, klass)",
                  assumptions = "klass.valid",
                  replaces = "doMemo")
  protected final Object doFine(Cell core,
    @Cached("getNockClass(core)") NockClass klass,
    @Cached("getArm(core)") CallTarget arm) {
    return fast(arm, core);
  }

  @Specialization(replaces = "doFine")
  protected final Object doSlow(Cell core) {
    return fast(getArm(core), core);
  }

  @Fallback
  protected final Object doWrong(Object object) {
    throw new NockException("atomic core", this);
  }

  protected final Object fast(CallTarget target, Cell core) {
    return dispatchNode.executeDispatch(target, core);
  }

  @TruffleBoundary
  protected final CallTarget getArm(Cell core) {
    try {
      return core.getMeta().getNockClass(core, astContext.dashboard)
        .getArm(core, armAxis, slotNode, astContext);
    }
    catch ( ExitException e ) {
      throw new NockException("fail to fetch arm from battery", e, this);
    }
  }

  @TruffleBoundary
  protected final boolean same(Cell a, Cell b) {
    return Equality.equals(a, b);
  }

  @TruffleBoundary
  protected final boolean fine(Cell core, NockClass klass) {
    return klass.getFine(core).check(core, astContext.dashboard);
  }
}
