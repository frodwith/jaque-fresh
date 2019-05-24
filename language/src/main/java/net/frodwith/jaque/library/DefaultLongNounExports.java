package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;

@ExportLibrary(value=NounLibrary.class, receiverType=Long.class)
final class DefaultLongNounExports {
  @ExportMessage
  static boolean isNoun(@SuppressWarnings("unused") Long receiver) {
    return true;
  }

  @ExportMessage
  static int mug(Long receiver) {
    return Mug.get(receiver);
  }
}
