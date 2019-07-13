package net.frodwith.jaque.data;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.library.NounLibrary;
import net.frodwith.jaque.exception.ExitException;

@ExportLibrary(NounLibrary.class)
public final class DynamicCell {
  Object head, tail, meta;

  public DynamicCell(Object head, Object tail) {
    this.head = head;
    this.tail = tail;
    this.meta = null;
  }

  @ExportMessage boolean isNoun() {
    return true;
  }

  @ExportMessage boolean isCell() {
    return true;
  }

  @ExportMessage Object head() {
    return head;
  }

  @ExportMessage Object tail() {
    return tail;
  }

  @ExportMessage int cachedMug(
    @CachedLibrary(limit="3") NounLibrary nouns) {
    return ( null == meta )
      ? 0
      : ( meta instanceof Integer)
      ? (int) meta
      : nouns.cachedMug(meta);
  }

  private boolean hasMeta() {
    return null != meta && !(meta instanceof Integer);
  }

  @ExportMessage void teach(Object other,
    @CachedLibrary(limit="3") NounLibrary nouns) {
    if ( hasMeta() ) {
      nouns.teach(meta, other);
    }
    else {
      nouns.learnHead(other, head);
      nouns.learnTail(other, tail);
      if ( null != mug ) {
        nouns.learnMug(other, mug);
      }
    }
  }

  @ExportMessage void learnConstantCell(ConstantCell k,
    @CachedLibrary(limit="3") NounLibrary nouns) {
    head = k.head;
    tail = k.tail;
    if ( hasMeta() ) {
      nouns.learnConstantCell(meta, k);
    }
    else {
      meta = k;
    }
  }

  @ExportMessage void learnHead(Object head,
    CachedLibrary(limit="3") NounLibrary nouns) {
    this.head = head;
    if ( hasMeta() ) {
      nouns.learnHead(meta, head);
    }
  }

  @ExportMessage void learnTail(Object tail,
    CachedLibrary(limit="3") NounLibrary nouns) {
    this.tail = tail;
    if ( hasMeta() ) {
      nouns.learnTail(meta, tail);
    }
  }

  @ExportMessage ConstantCell knownConstantCell(
    @CachedLibrary(limit="3") NounLibrary nouns) {
    return ( null == meta || meta instanceof Integer )
      ? null
      : nouns.knownConstantCell(meta);
  }

  @ExportMessage int mug(@CachedLibrary(limit="3") NounLibrary nouns) {
    if ( null == meta ) {
      int mug = Mug.both(nouns.mug(head), nouns.mug(tail));
      meta = mug;
      return mug;
    }
    else if ( meta instanceof Integer ) {
      return (int) meta;
    }
    else {
      return nouns.mug(meta);
    }
  }

  @ExportMessage void learnMug(int mug,
    @CachedLibrary(limit="3") NounLibrary nouns) {
    if ( hasMeta() ) {
      nouns.learnMug(meta, mug);
    }
    else {
      meta = mug;
    }
  }

  @ExportMessage Object cleanup(@CachedLibrary(limit="3") NounLibrary nouns) {
    if ( null == meta || meta instanceof Integer ) {
      head = nouns.cleanup(head);
      tail = nouns.cleanup(tail);
      return this;
    }
    else {
      meta = nouns.cleanup(meta);
      try {
        head = nouns.head(meta);
        tail = nouns.tail(meta);
        return meta;
      }
      catch ( ExitException e ) {
        throw new AssertionError("cleanup fragment failed!");
      }
    }
  }
}
