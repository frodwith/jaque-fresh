package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockContext;

@NodeChild(value="cellNode", type=NockExpressionNode.class)
@NodeField(name="contextReference", type=ContextReference.class)
public abstract class NockFunctionLookupNode extends NockNode {
  public static final int INLINE_CACHE_SIZE = 2;
  protected abstract ContextReference<NockContext> getContextReference();
  public abstract NockFunction executeLookup(VirtualFrame frame);

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "cachedFormula == formula")
  protected NockFunction doCached(Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("lookup(formula)") NockFunction cachedFunction) {
    return cachedFunction;
  }

  @Specialization
  protected NockFunction doUncached(Cell formula) {
    return lookup(formula);
  }

  @TruffleBoundary
  protected NockFunction lookup(Cell formula) {
    try {
      return formula.getMeta()
        .getFunction(getContextReference().get().functionRegistry);
    }
    catch (Fail e) {
      throw new Bail("bad formula", this);
    }
  }
  
  @Fallback
  protected NockFunction doAtom(Object atom) {
    throw new Bail("atom not formula", this);
  }
}
