package net.frodwith.jaque.nodes.jet.ut;

import java.util.Objects;

import net.frodwith.jaque.runtime.Equality;

public final class VetSutRefKey {
  private String id;
  private boolean vet;
  private Object sut, ref;

  public VetSutRefKey(String id, boolean vet, Object sut, Object ref) {
    this.id  = id;
    this.vet = vet;
    this.sut = sut;
    this.ref = ref;
  }

  public boolean equals(Object other) {
    if ( !(other instanceof VetSutRefKey) ) {
      return false;
    }
    else {
      VetSutRefKey v = (VetSutRefKey) other;
      return vet == v.vet
        && id.equals(v.id)
        && Equality.equals(sut, v.sut)
        && Equality.equals(ref, v.ref);
    }
  }

  public int hashCode() {
    return Objects.hash(id, vet, sut, ref);
  }
}
