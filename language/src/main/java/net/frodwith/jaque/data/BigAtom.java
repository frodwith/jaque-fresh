package net.frodwith.jaque.data;

import java.io.Serializable;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;

public final class BigAtom implements TruffleObject, Serializable {
  public int[] words;
  public int mug;

  public BigAtom(int[] words) {
    // smaller atoms must be represented by longs
    assert(words.length > 2);

    this.words = words;
    this.mug   = 0;
  }

  public int hashCode() {
    return Mug.get(this);
  }

  public boolean equals(Object o) {
    return (o instanceof BigAtom) && Equality.equals(this, (BigAtom) o);
  }

  public ForeignAccess getForeignAccess() {
    return BigAtomMessageResolutionForeign.ACCESS;
  }
}
