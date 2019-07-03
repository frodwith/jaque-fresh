package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.exception.ExitException;

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
  static boolean fitsInBoolean(Integer receiver) {
    switch ( receiver ) {
      case 0:
        return true;
      case 1:
        return true;
      default:
        return false;
    }
  }

  @ExportMessage
  static boolean asBoolean(Integer receiver) throws ExitException {
    switch ( receiver ) {
      case 0:
        return true;
      case 1:
        return false;
      default:
        throw new ExitException("not a boolean");
    }
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
