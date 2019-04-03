package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.exception.ExitException;

public final class CellMeta {
  private int mug;

  private Optional<NockFunction> function;
  private Optional<NockObject> object;
  private Optional<CellGrain> grain;

  public CellMeta(int mug) {
    this.mug       = mug;
    this.klass     = Optional.empty();
    this.function  = Optional.empty();
    this.grain     = Optional.empty();
  }

  public static void unify(CellMeta a, CellMeta b) {
    // FIXME: do nothing
  }

  /*
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
  }
  */

  // mugs

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

  // grains

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

  // don't call unless you know you have a grain.
  public CellGrain getGrain() {
    return grain.get();
  }

  // cell-as-formula

  public boolean hasFunction(AstContext context) {
    return function.isPresent() && function.get().compatible(context);
  }

  public NockFunction 
    getFunction(Cell cell, AstContext context)
      throws ExitException {
    NockFunction f;
    if ( function.isPresent() ) {
      f = function.get();
      if ( !f.compatible(context) ) {
        f = f.forContext(context);
        function = Optional.of(f);
      }
    }
    else {
      f = context.getFunction(cell);
      function = Optional.of(f);
    }
    return f;
  }

  // cell-as-core

  public boolean hasObject(AstContext context) {
    return object.isPresent() && object.get().compatible(context);
  }

  public boolean knownAt(Location location, Dashboard dashboard) {
    if ( object.isPresent() ) {
      NockObject o = object.get();
      return o.dashboardCompatible(dashboard) && o.locatedAt(location);
    }
    else {
      return false;
    }
  }

  public Optional<NockObject> cachedObject(AstContext context) {
    return hasObject(context) ? object : Optional.empty();
  }

  public void setObject(NockObject object) {
    this.object = Optional.of(object);
  }

  public NockObject 
    getObject(Cell core, AstContext context) 
      throws ExitException {
    NockObject o;

    if ( object.isPresent() ) {
      o = object.get();
      if ( o.compatible(context) ) {
        return o;
      }
      else {
        o = o.recontextualize(context);
      }
    }
    else {
      o = context.getObject(core);
    }

    object = Optional.of(o);
    return o;
  }

  public void
    copyObjectToMutant(Cell core, Cell mutant, Axis written, AstContext context) {
    try {
      if ( hasObject(context) ) {
        NockObject o = object.get();
        Cell battery = Cell.require(core.head);
        if ( o.copyableEdit(written, battery) ) {
          mutant.getMeta().setObject(o);
        }
      }
    }
    catch ( ExitException e) {
    }
  }
}
