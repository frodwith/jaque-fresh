package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.FormulaCompiler;
import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.exception.ExitException;

public final class CellMeta {
  private int mug;

  private Optional<NockFunction> function;
  private NockObject object;
  private Optional<CellGrain> grain;

  public CellMeta(int mug) {
    this.mug       = mug;
    // TODO: use optional for all three of these
    this.object    = null;
    this.function  = Optional.empty();
    this.grain     = Optional.empty();
  }

  public boolean inSilo(GrainSilo silo) {
    return grain.isPresent() && grain.get().inSilo(silo);
  }

  public void setSilo(GrainSilo silo) {
    if ( grain.isPresent() ) {
      grain.get().setSilo(silo);
    }
    else {
      grain = Optional.of(new CellGrain(silo));
    }
  }

  public NockObject getObject(NockContext context, Cell cell) throws ExitException {
    if ( !hasObject() ) {
      object = context.dashboard.getObject(cell);
    }
    return object;
  }

  public void setObject(NockObject object) {
    this.object = object;
  }

  public boolean hasObject() {
    if ( null == object ) {
      return false;
    }
    else if ( object.klass.valid.isValid() ) {
      return true;
    }
    else {
      this.object = null;
      return false;
    }
  }

  public NockObject cachedObject() {
    return hasObject() ? object : null;
  }

  public void writeObject(Cell edited, Axis written) {
    if ( hasObject() &&
        object.klass.copyableEdit((Cell) edited.head, written) ) {
      edited.getMeta().object = object.like(edited);
    }
  }

  public boolean knownAt(Location location) {
    return hasObject() && object.klass.locatedAt(location);
  }

  // don't call unless you know you have a grain.
  public CellGrain getGrain() {
    return grain.get();
  }

  public NockFunction getFunction(Dashboard dashboard, Cell cell)
    throws ExitException {
    boolean have = function.isPresent();
    NockFunction f = null;
    if ( have ) {
      f = function.get();
      have = f.ofDashboard(dashboard);
    }
    if ( !have ) {
      f = dashboard.compileFormula(cell);
      function = Optional.of(f);
    }
    return f;
  }

  public int mug(Cell cell) {
    if ( 0 == mug ) {
      mug = Mug.calculate(cell);
    }
    return mug;
  }

  public void setMug(int mug) {
    this.mug = mug;
  }

  public int cachedMug() {
    return mug;
  }

  public void unify(CellMeta other) {
    // FIXME: revisit this whole procedure, in particular need to think about
    // how grains unify
    boolean mine = false, his = false;

    // mugs
    if ( 0 == mug ) {
      mug = other.mug;
    }
    else if ( 0 == other.mug ) {
      other.mug = mug;
    }

    // functions
    if ( null == function ) {
      function = other.function;
      his = true;
    }
    else if ( null == other.function ) {
      other.function = function;
      mine = true;
    }

    // objects
    if ( null == object ) {
      object = other.object;
      his = true;
    }
    else {
      other.object = object;
      mine = true;
    }
  }
}
