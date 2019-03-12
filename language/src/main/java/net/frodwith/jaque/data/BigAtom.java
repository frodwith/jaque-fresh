package net.frodwith.jaque.data;

import java.io.StringWriter;
import java.io.IOException;
import java.io.Serializable;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import com.google.common.hash.HashCode;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.GrainSilo;
import net.frodwith.jaque.printer.SimpleAtomPrinter;

public final class BigAtom implements TruffleObject, Serializable {
  public static final BigAtom MINIMUM = new BigAtom(new int[] {0, 0, 1});

  public int[] words;
  private Object meta;

  public BigAtom(int[] words) {
    // smaller atoms must be represented by longs
    assert(words.length > 2);

    this.words = words;
    this.meta  = 0;
  }

  public int getMug() {
    if ( meta instanceof BigAtomMeta ) {
      return ((BigAtomMeta) meta).getMug(words);
    }
    else {
      int mug = (int) meta;
      if ( 0 == mug ) {
        mug = Mug.words(words);
        meta = mug;
      }
      return mug;
    }
  }

  public boolean inSilo(GrainSilo silo) {
    return ( meta instanceof BigAtomMeta ) && ((BigAtomMeta) meta).inSilo(silo);
  }

  private BigAtomMeta getMeta() {
    if ( !(meta instanceof Integer) ) {
      return (BigAtomMeta) meta;
    }
    else {
      BigAtomMeta newMeta = new BigAtomMeta((int) meta);
      meta = newMeta;
      return newMeta;
    }
  }

  public void setSilo(GrainSilo silo) {
    getMeta().setSilo(silo);
  }

  public int hashCode() {
    return getMug();
  }

  public int cachedMug() {
    if ( meta instanceof Integer ) {
      return (int) meta;
    }
    else {
      return ((BigAtomMeta) meta).cachedMug();
    }
  }

  public boolean unequalMugs(BigAtom other) {
    int a = cachedMug();
    if ( 0 == a ) {
      return false;
    }
    int b = other.cachedMug();
    if ( 0 == b ) {
      return false;
    }
    return a != b;
  }

  public boolean equals(Object o) {
    return (o instanceof BigAtom) && Equality.equals(this, (BigAtom) o);
  }

  public ForeignAccess getForeignAccess() {
    return BigAtomMessageResolutionForeign.ACCESS;
  }

  public HashCode getStrongHash() {
    return getMeta().getStrongHash(asByteArray());
  }

  public byte[] asByteArray() {
    return Atom.wordsToBytes(words, HoonMath.met((byte) 3, this));
  }

  public void unify(BigAtom other) {
    words = other.words;
    if ( meta instanceof Integer ) {
      if ( other.meta instanceof Integer ) {
        int  m = (int) meta,
            om = (int) other.meta;
        if ( 0 == m ) {
          meta = om;
        }
        else if ( 0 == om ) {
          other.meta = m;
        }
      }
      else {
        unifyIntWithMeta(this, other);
      }
    }
    else if ( other.meta instanceof Integer ) {
      unifyIntWithMeta(other, this);
    }
    else {
      ((BigAtomMeta) meta).unify((BigAtomMeta) other.meta);
    }
  }

  private static void unifyIntWithMeta(BigAtom i, BigAtom m) {
    int mug = (int) i.meta;
    if ( 0 != mug ) {
      ((BigAtomMeta) m.meta).setMug(mug);
    }
  }

  // for debugging
  public String pretty() {
    StringWriter out = new StringWriter();
    try {
      SimpleAtomPrinter.raw(out, words, 16, 0);
      return out.toString();
    }
    catch ( IOException e ) {
      return "noun misprint";
    }
  }
}
