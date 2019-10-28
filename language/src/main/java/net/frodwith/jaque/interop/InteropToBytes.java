package net.frodwith.jaque.interop;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;

@ExportLibrary(InteropLibrary.class)
public final class InteropToBytes implements TruffleObject {
  @TruffleBoundary
  private static byte[] toByteArray(Object obj) {
    return Atom.toByteArray(obj, Atom.BIG_ENDIAN);
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments)
      throws ArityException, UnsupportedTypeException {
    if ( 1 != arguments.length ) {
      throw ArityException.create(1, arguments.length);
    }
    else {
      try {
        return new ByteArray(toByteArray(arguments[0]));
      }
      catch ( IllegalArgumentException e ) {
        throw UnsupportedTypeException.create(arguments);
      }
    }
  }

  /**
   * Needed to pass the byte array across the truffle polyglot boundary.
   */
  @ExportLibrary(InteropLibrary.class)
  class ByteArray implements TruffleObject {
    private final byte[] bytes;

    ByteArray(byte[] bytes) {
      this.bytes = bytes;
    }

    @ExportMessage
    public boolean hasArrayElements() {
      return true;
    }

    @ExportMessage
    public long getArraySize() {
      return bytes.length;
    }

    @ExportMessage
    public boolean isArrayElementReadable(long index) {
      return index < bytes.length;
    }

    @ExportMessage
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
      if ( index > bytes.length ) {
        throw InvalidArrayIndexException.create(index);
      }
      else {
        return bytes[(int)index];
      }
    }
  }
}
