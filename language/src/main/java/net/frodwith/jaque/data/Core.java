package net.frodwith.jaque.data;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

// meta:
//   ConstantCell - there is an interned constant for this cell. Those have a
//   lazy core.
//
//   CoreInfo - Assumption (from battery)
//              CorePattern (nullable)
//              mug
//     getCorePattern() -- uses cached until assumption breaks
//    probably need a learnCoreInfo() thing in NounLibrary
//
//   Integer - just a mug
@ExportLibrary(NounLibrary.class)
public final class Core {
  final ConstantCell battery;
  Object payload, meta;
  Assumption stable;

  CorePattern verified;

  public Core(ConstantCell battery, Object payload) {
    this.battery = battery;
    this.payload = payload;
  }

  public Core(ConstantCell battery, Object payload, ConstantCell interned) {
    this(battery, payload);
    meta = interned;
  }

  public Core(ConstantCell battery, Object payload, CoreInfo info) {
    this(battery, payload);
    meta = info;
  }

  public Core(ConstantCell battery, Object payload, int mug) {
    this(battery, payload);
    meta = mug;
  }

  @ExportMessage boolean isNoun() {
    return true;
  }

  @ExportMessage boolean isCell() {
    return true;
  }

  @ExportMessage Object head() {
    return battery;
  }

  @ExportMessage Object tail() {
    return payload;
  }

  @ExportMessage void learnTail(Object tail) {
    this.payload = tail;
  }

  @ExportMessage void teach(Object other,
    @CachedLibrary(limit="3") NounLibrary nouns) {
    if ( meta instanceof ConstantCell ) {
      nouns.teach(meta, other);
    }
    else {
      nouns.learnHead(other, battery);
      nouns.learnTail(other, payload);
      if ( null != meta ) {
        nouns.learnMug(other, (int) meta);
      }
    }
  }

  @ExportMessage void learnConstantCell(ConstantCell k,
    @CachedLibrary(limit="3") NounLibrary nouns) {
    if ( !(meta instanceof ConstantCell) ) {
      payload = k.tail;
      meta = k;
    }
  }

  @ExportMessage ConstantCell knownConstantCell(
    @CachedLibrary(limit="3") NounLibrary nouns) {
    return ( meta instanceof ConstantCell )
      ? (ConstantCell) meta
      : null;
  }
}
