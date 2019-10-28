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
public final class InteropFromBytes implements TruffleObject {
  @TruffleBoundary
  private static Object fromByteArray(byte[] bytes) {
    return Atom.fromByteArray(bytes, Atom.BIG_ENDIAN);
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
      if (arguments[0] instanceof byte[]) {
        return fromByteArray((byte[])arguments[0]);
      }
      else {
        throw UnsupportedTypeException.create(arguments);
      }
    }
  }
}
