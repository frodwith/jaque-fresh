package net.frodwith.jaque.util;

import java.util.HashMap;

import com.oracle.truffle.api.CompilerAsserts;

import net.frodwith.jaque.library.NounLibrary;

// This is a slow-path map (using the uncached NounLibrary) with arbitrary
// nouns as keys. It must not be included in compiled code, so all usage should
// be put behind a TruffleBoundary.
public final class NounMap<T> {
  private HashMap<Key,T> hash;

  public NounMap() {
    hash = new HashMap<>();
  }

  public void put(Object key, T value) {
    CompilerAsserts.neverPartOfCompilation();
    hash.put(new Key(key), value);
  }

  public T get(Object key) {
    CompilerAsserts.neverPartOfCompilation();
    return hash.get(new Key(key));
  }

  private static class Key {
    final Object noun;
    final NounLibrary nouns;

    Key(Object noun) {
      this.noun = noun;
      this.nouns = NounLibrary.getUncached(noun);
    }

    @Override
    public Object equals(Object other) {
      CompilerAsserts.neverPartOfCompilation();
      return nouns.unifyEquals(noun, other);
    }

    @Override
    public int hashCode() {
      CompilerAsserts.neverPartOfCompilation();
      return nouns.mug(noun);
    }
  }
}
