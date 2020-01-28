package net.frodwith.jaque.nodes.op;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.nodes.NockNode;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

public abstract class FormulaLookupOpNode extends NockNode {
  private AstContext astContext;

  protected FormulaLookupOpNode(AstContext astContext) {
    this.astContext = astContext;
  }

  public abstract CallTarget executeLookup(Object formula);

  @Specialization(limit = "2", guards = "same(formula, cachedFormula)")
  protected CallTarget cached(Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("lookup(formula)") CallTarget cachedTarget) {
    return cachedTarget;
  }

  @Specialization
  protected CallTarget uncached(Cell formula) {
    return lookup(formula);
  }

  @Fallback
  protected CallTarget other(Object atom) {
    throw new NockException("invalid formula", this);
  }

  @TruffleBoundary
  protected CallTarget lookup(Cell formula) {
    try {
      return formula.getMeta().getFunction(formula, astContext).callTarget;
    }
    catch (ExitException e) {
      throw new NockException("invalid formula", e, this);
    }
  }

  @TruffleBoundary
  protected boolean same(Cell a, Cell b) {
    return Equality.equals(a, b);
  }
}
