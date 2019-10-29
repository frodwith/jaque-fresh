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

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;

@ExportLibrary(InteropLibrary.class)
public final class InteropFromBytes implements TruffleObject {
  @TruffleBoundary
  private static Object fromByteArray(Object bytes) {
      return Atom.fromByteArray((byte[])bytes, Atom.LITTLE_ENDIAN);
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments)
      throws ArityException, UnsupportedTypeException {
    if ( 2 != arguments.length ) {
      throw ArityException.create(2, arguments.length);
    }
    else {
      //
      NockContext context = (NockContext)arguments[0];
      Object[] origArguments = (Object[])arguments[1];

      return fromByteArray(context.asHostObject(origArguments[0]));
    }
  }
}
