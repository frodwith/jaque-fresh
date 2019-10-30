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

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.exception.ExitException;

@ExportLibrary(InteropLibrary.class)
public final class InteropMug implements TruffleObject {
  @TruffleBoundary
  private static int mug(Object obj) {
    // TODO: The problem with this implementation is that we aren't caching the
    // mugs on the cell object or BigAtom objects. I assume this will be fixed
    // in the new data representation where we're able to call `value.mug()` on
    // value classes.
    if (obj instanceof BigAtom) {
      return ((BigAtom)obj).getMug();
    } else if (obj instanceof Cell) {
      return ((Cell)obj).mug();
    } else {
      // Fallback
      return Murmug.get(obj);
    }
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments)
      throws ArityException {
    if ( 1 != arguments.length ) {
      throw ArityException.create(1, arguments.length);
    }
    else {
      return mug(arguments[0]);
    }
  }
}
