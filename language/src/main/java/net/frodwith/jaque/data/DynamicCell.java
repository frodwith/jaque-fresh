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
