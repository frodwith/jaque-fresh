package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.NockFunction;
import net.frodwith.jaque.exception.ExitException;

public final class CellMeta {
  private int mug;

  private Optional<NockFunction> function;
  private Optional<NockClass> klass;
  private Optional<CellGrain> grain;

  public CellMeta(int mug) {
    this.mug       = mug;
    this.klass     = Optional.empty();
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

  public NockClass getClass(Cell core, Dashboard dashboard) throws ExitException {
    if ( hasClass(dashboard) ) {
      return klass.get();
    }
    else {
      NockClass k = dashboard.getClass(core);
      klass = Optional.of(k);
      return k;
    }
  }

  public void setClass(NockClass klass) {
    this.klass = Optional.of(klass);
  }

  public boolean hasClass(Dashboard dashboard) {
    return klass.isPresent() && klass.get().ofDashboard(dashboard);
  }

  public Optional<NockClass> cachedClass(Dashboard dashboard) {
    return hasClass(dashboard) ? klass : Optional.empty();
  }

  public void
    copyClassToMutant(Cell core, Cell mutant, Axis written, Dashboard dashboard) {
    try {
      if ( hasClass(dashboard) ) {
        NockClass c = klass.get();
        Cell battery = Cell.require(core.head);
        if ( c.copyableEdit(written, battery) ) {
          mutant.getMeta().setClass(c);
        }
      }
    }
    catch ( ExitException e) {
    }
  }

  public boolean knownAt(Location location, Dashboard dashboard) {
    return hasClass(dashboard) && klass.get().locatedAt(location);
  }

  // don't call unless you know you have a grain.
  public CellGrain getGrain() {
    return grain.get();
  }

  public CallTarget getFunction(Cell cell, Dashboard dashboard)
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
    return f.callTarget;
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

    // Double TODO-ado!
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

    /* TRIPLE Todo-ado!
    // objects
    if ( null == object ) {
      object = other.object;
      his = true;
    }
    else {
      other.object = object;
      mine = true;
    }
    */
  }
}
