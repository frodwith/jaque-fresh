package net.frodwith.jaque.data;

import java.io.StringWriter;
import java.io.IOException;
import java.io.Serializable;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.printer.SimpleAtomPrinter;

public final class BigAtom implements TruffleObject, Serializable {
  public static final BigAtom MINIMUM = new BigAtom(new int[] {0, 0, 1});

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
