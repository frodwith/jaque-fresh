package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

@NodeChild(value="cellNode", type=SubjectNode.class)
@NodeField(name="astContext", type=AstContext.class)
public abstract class NockFunctionLookupNode extends NockNode {
  public static final int INLINE_CACHE_SIZE = 2;
  protected abstract AstContext getAstContext();
  public abstract CallTarget executeLookup(VirtualFrame frame);

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "cachedFormula == formula")
  protected CallTarget doCached(Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("lookup(formula)") CallTarget cachedFunction) {
    return cachedFunction;
  }

  @Specialization
  protected CallTarget doUncached(Cell formula) {
    return lookup(formula);
  }

  @TruffleBoundary
  protected CallTarget lookup(Cell formula) {
    try {
      return formula.getMeta()
        .getFunction(formula, getAstContext())
        .callTarget;
    }
    catch (ExitException e) {
      throw new NockException("bad formula", e, this);
    }
  }
  
  @Fallback
  protected CallTarget doAtom(Object atom) {
    throw new NockException("atom not formula: " + atom, this);
  }
}
