package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;

public abstract class NockFunctionLookupNode extends NockLookupNode {
  public abstract NockFunction executeLookup();

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "sameCells(cachedFormula, formula)")
  protected NockFunction doCached(Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("lookup(formula)") NockFunction cachedFunction) {
    return cachedFunction;
  }

  @Specialization(replaces = "doCached")
  protected NockFunction doUncached(Cell formula) {
    return lookup(formula);
  }

  @TruffleBoundary
  protected NockFunction lookup(Cell formula) {
    try {
      return getContextReference().get().lookupFunction(formula);
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
