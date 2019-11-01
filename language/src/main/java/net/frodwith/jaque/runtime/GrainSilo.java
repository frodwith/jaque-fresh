package net.frodwith.jaque.runtime;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class GrainSilo {
  private final Interner<BigAtom> atoms;
  private final Interner<Cell> cells;

  public GrainSilo() {
    this.atoms = Interners.newWeakInterner();
    this.cells = Interners.newWeakInterner();
  }

  @TruffleBoundary
  public Cell getCellGrain(Cell cell) {
    if ( !cell.inSilo(this) ) {
      cell = cells.intern(cell);
      cell.setSilo(this);
    }
    return cell;
  }

  @TruffleBoundary
  public BigAtom getAtomGrain(BigAtom atom) {
    if ( !atom.inSilo(this) ) {
      atom = atoms.intern(atom);
      atom.setSilo(this);
    }
    return atom;
  }

  public Object getNounGrain(Object noun) {
    return ( noun instanceof Long ) ? noun
      : ( noun instanceof BigAtom )
      ? getAtomGrain((BigAtom) noun)
      : getCellGrain((Cell) noun);
  }
}
