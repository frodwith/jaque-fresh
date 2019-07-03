package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.exception.ExitException;

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
  static boolean fitsInBoolean(Long receiver) {
    return 0L == receiver || 1L == receiver;
  }

  @ExportMessage
  static boolean asBoolean(Long receiver) throws ExitException {
    if ( 0L == receiver ) {
      return true;
    }
    else if ( 1L == receiver ) {
      return false;
    }
    else {
      throw new ExitException("not a boolean");
    }
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
  static long bitLength(Long receiver) {
    return 64 - Long.numberOfLeadingZeros(receiver);
  }

  @ExportMessage
  static boolean testBit(Long receiver, long index) {
    return 0 != (receiver & (1L << index));
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
