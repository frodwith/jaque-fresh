package net.frodwith.jaque.data;

import java.util.Optional;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.runtime.HoonSerial;

public final class CellGrain {
  private static final HashFunction sha = Hashing.sha256();

  private GrainSilo silo;
  private Optional<HashCode> hash;

  public CellGrain(GrainSilo silo) {
    this.silo = silo;
  }

  public boolean inSilo(GrainSilo silo) {
    return silo == this.silo;
  }

  public void setSilo(GrainSilo silo) {
    this.silo = silo;
  }

  public HashCode getStrongHash(Cell cell) {
    if ( !hash.isPresent() ) {
      hash = Optional.of(sha.hashBytes(HoonSerial.jamBytes(cell)));
    }
    return hash.get();
  }
}
