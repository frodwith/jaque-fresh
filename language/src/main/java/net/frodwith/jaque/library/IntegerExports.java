package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;

@ExportLibrary(value=NounLibrary.class, receiverType=Integer.class)
final class IntegerExports {
  @ExportMessage
  static boolean isNoun(Integer receiver) {
    return true;
  }

  @ExportMessage
  static boolean isAtom(Integer receiver) {
    return true;
  }

  @ExportMessage
  static boolean fitsInInt(Integer receiver) {
    return true;
  }

  @ExportMessage
  static boolean fitsInLong(Integer receiver) {
    return true;
  }

  @ExportMessage
  static int asInt(Integer receiver) {
    return receiver;
  }

  @ExportMessage
  static long asLong(Integer receiver) {
    return Integer.toUnsignedLong(receiver);
  }

  @ExportMessage
  static long bitLength(Integer receiver) {
    return 32 - Integer.numberOfLeadingZeros(receiver);
  }

  @ExportMessage
  static boolean testBit(Integer receiver, long index) {
    return 0 != (receiver & (1 << index));
  }

  @ExportMessage
  static int[] asIntArray(Integer receiver) {
    return new int[] { receiver };
  }

  @ExportMessage
  static int mug(Integer receiver) {
    return Mug.words(asIntArray(receiver));
  }
}
