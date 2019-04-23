package net.frodwith.jaque.interop;

import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;

@ExportLibrary(InteropLibrary.class)
public final class InteropArray implements TruffleObject {
  private final Object[] objects;

  public InteropArray(Object... objects) {
    this.objects = objects;
  }

  @ExportMessage
  public boolean hasArrayElements() {
    return true;
  }

  @ExportMessage
  public boolean isArrayElementReadable(long index) {
    return index >= 0 && index < objects.length;
  }

  @ExportMessage
  public long getArraySize() {
    return objects.length;
  }

  @ExportMessage
  public Object readArrayElement(long index) throws InvalidArrayIndexException {
    if ( index < objects.length ) {
      return objects[(int) index];
    }
    else {
      throw InvalidArrayIndexException.create(index);
    }
  }
}
