package net.frodwith.jaque;

import java.util.Objects;
import java.util.Arrays;

import net.frodwith.jaque.runtime.Equality;

public final class NounsKey {
  private String id;
  private Object[] nouns;

  public NounsKey(String id, Object[] nouns) {
    this.id  = id;
    this.nouns = nouns;
  }

  public boolean equals(Object other) {
    if ( !(other instanceof NounsKey) ) {
      return false;
    }
    else {
      NounsKey ok = (NounsKey) other;

      if ( (!id.equals(ok.id))
        || (nouns.length != ok.nouns.length) ) {
        return false;
      }

      for ( int i = 0; i < nouns.length; ++i ) {
        if ( !Equality.equals(nouns[i], ok.nouns[i]) ) {
          return false;
        }
      }

      return true;
    }
  }

  public int hashCode() {
    return Objects.hash(id, Arrays.hashCode(nouns));
  }
}
