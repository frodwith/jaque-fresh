package net.frodwith.jaque.runtime;

import net.frodwith.jaque.data.Cell;

// a map key that holds a strong reference to a grained cell (preventing it from
// being garbage collected) and compares equality to other StrongCellGrainKeys
// by pointer equality of the wrapped cell.

public final class StrongCellGrainKey {
  private final Cell grain;
  private final int hashCode;

  public StrongCellGrainKey(Cell grain) {
    this.grain = grain;
    this.hashCode = grain.hashCode();
  }

  public boolean equals(Object other) {
    return (other instanceof StrongCellGrainKey)
      && (this.grain == ((StrongCellGrainKey) other).grain);
  }

  public int hashCode() {
    return hashCode;
  }
}
