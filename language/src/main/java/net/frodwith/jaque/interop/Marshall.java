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

import net.frodwith.jaque.data.Cell;

@ExportLibrary(InteropLibrary.class)
public final class Marshall implements TruffleObject {

  @TruffleBoundary
  private static Object objectToNoun(Object obj, InteropLibrary interop) {
    try {
      if ( interop.fitsInLong(obj) ) {
        return interop.asLong(obj);
      }
      else if ( interop.hasArrayElements(obj) ) {
        long size = interop.getArraySize(obj);
        if ( size > 1 ) {
          Object head = interop.readArrayElement(obj, size-1),
                 tail = objectToNoun(head, interop);
          for ( long i = size - 2; i >= 0; --i ) {
            head = interop.readArrayElement(obj, i);
            tail = new Cell(objectToNoun(head, interop), tail);
          }
          return tail;
        }
      }
    }
    catch ( InvalidArrayIndexException | UnsupportedMessageException e ) {
    }
    throw new IllegalArgumentException();
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments,
    @CachedLibrary(limit="3") InteropLibrary interop)
      throws ArityException, UnsupportedTypeException {
    if ( 0 == arguments.length ) {
      throw ArityException.create(1, 0);
    }
    else {
      try {
        int size = arguments.length;
        Object head, tail = objectToNoun(arguments[size-1], interop);
        for ( int i = size-2; i >= 0; --i ) {
          head = objectToNoun(arguments[i], interop);
          tail = new Cell(head, tail);
        }
        return tail;
      }
      catch ( IllegalArgumentException e ) {
        throw UnsupportedTypeException.create(arguments);
      }
    }
  }
}
