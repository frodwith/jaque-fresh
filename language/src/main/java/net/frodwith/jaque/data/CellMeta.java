package net.frodwith.jaque.data;

import java.util.Optional;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.jet.Drivers;
import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.dashboard.Battery;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.dashboard.LocatedClass;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.nodes.FragmentNode;

public final class CellMeta {
  private int mug;
  private Optional<NockClass> klass;
  private Optional<NockFunction> function;
  private Optional<CellGrain> grain;

  public CellMeta(int mug) {
    this.mug       = mug;
    this.klass     = Optional.empty();
    this.function  = Optional.empty();
    this.grain     = Optional.empty();
  }

  // metadata unification

  public static void unify(CellMeta a, CellMeta b) {
    if ( a.klass.isPresent() ) {
      if ( !b.klass.isPresent() ) {
        b.klass = a.klass;
      }
    }
    else if ( b.klass.isPresent() ) {
      a.klass = b.klass;
    }
    // FIXME: do something for functions
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

  // mugs (cached hashes)
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

  // grains (deduplicated, lazily-strongly hashed nouns)
  public CellGrain getGrain() {
    return grain.get();
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

  // cell-as-nock-formula (AstContext specific)
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
  public NockClass
    getNockClass(Cell core, Dashboard dashboard) 
      throws ExitException {
    NockClass k;
    if ( klass.isPresent() ) {
      k = klass.get();
      if ( k.isValid(dashboard) ) {
        return k;
      }
    }
    k = dashboard.getNockClass(core);
    klass = Optional.of(k);
    return k;
  }

  public void setNockClass(NockClass klass) {
    this.klass = Optional.of(klass);
  }

  public boolean hasClass(Dashboard dashboard) {
    return klass.isPresent() && klass.get().isValid(dashboard);
  }

  public Optional<NockClass> cachedClass(Dashboard dashboard) {
    return hasClass(dashboard) ? klass : Optional.empty();
  }

  public boolean knownAt(Location location, Dashboard dashboard) {
    if ( hasClass(dashboard) ) {
      return klass.get().locatedAt(location);
    }
    else {
      return false;
    }
  }

  // copy metadata objects (if possible) after an edit (nock #)
  public void
    copyMetaToMutant(Cell core, Cell mutant, Axis written, Dashboard dashboard) {
    try {
      if ( hasClass(dashboard) ) {
        NockClass c = klass.get();
        Cell battery = Cell.require(core.head);
        if ( c.copyableEdit(written, battery) ) {
          CellMeta mutantMeta = mutant.getMeta();
          mutantMeta.klass = klass;
        }
      }
    }
    catch ( ExitException e) {
    }
  }

}
