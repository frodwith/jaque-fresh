package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;

@ExportLibrary(value=NounLibrary.class, receiverType=Long.class)
@ExportLibrary(value=AtomLibrary.class, receiverType=Long.class)
final class LongExports {
  @ExportMessage
  static boolean isNoun(Long receiver) {
    return true;
  }

  @ExportMessage
  static boolean fitsInLong(Long receiver) {
    return true;
  }

  @ExportMessage
  static long asLong(Long receiver) {
    return receiver;
  }

  @ExportMessage
  static int mug(Long receiver) {
    return Mug.get(receiver);
  }
}
