package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;

@ExportLibrary(value=NounLibrary.class, receiverType=Long.class)
final class LongExports {
  @ExportMessage
  static boolean isNoun(Long receiver) {
    return true;
  }

  @ExportMessage
  static boolean isAtom(Long receiver) {
    return true;
  }

  @ExportMessage
  static boolean fitsInInt(Long receiver) {
    return Long.compareUnsigned(receiver, 0xFFFFFFFFL) <= 0;
  }

  @ExportMessage
  static int asInt(Long receiver) {
    assert( fitsInInt(receiver) );
    return (int) (long) receiver;
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
  static int[] asIntArray(Long receiver) {
    int low = (int) (long) receiver;
    int high = (int) (receiver >>> 32);
    return ( high == 0 )
      ? new int[] { low }
      : new int[] { low, high };
  }

  @ExportMessage
  static int mug(Long receiver) {
    return Mug.get(receiver);
  }
}
