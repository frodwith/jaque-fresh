package net.frodwith.jaque.data;

import java.util.Optional;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.GrainSilo;

public final class BigAtomMeta {
  private static final HashFunction sha = Hashing.sha256();

  // This is the data stored "behind the pointer" on "fat" atoms (grains)
  private GrainSilo silo;
  private Optional<HashCode> hash;
  private int mug;

  public BigAtomMeta(int mug) {
    this.mug = mug;
  }

  public int getMug(byte[] words) {
    if ( 0 == mug ) {
      mug = Murmug.bytes(words);
    }
    return mug;
  }

  public void setMug(int mug) {
    this.mug = mug;
  }

  public int cachedMug() {
    return mug;
  }

  public boolean inSilo(GrainSilo silo) {
    return silo == this.silo;
  }

  public void setSilo(GrainSilo silo) {
    this.silo = silo;
  }

  public void unify(BigAtomMeta other) {
    if ( 0 == mug ) {
      mug = other.mug;
    }
    else if ( 0 == other.mug ) {
      other.mug = mug;
    }

    if ( !hash.isPresent() ) {
      hash = other.hash;
    }
    else if ( !other.hash.isPresent() ) {
      other.hash = hash;
    }
  }

  public HashCode getStrongHash(byte[] bytes) {
    if ( !hash.isPresent() ) {
      hash = Optional.of(sha.hashBytes(bytes));
    }
    return hash.get();
  }
}
