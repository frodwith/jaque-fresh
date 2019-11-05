package net.frodwith.jaque.interop;

import java.io.StringWriter;
import java.io.IOException;
import java.io.Writer;

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
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.printer.SimpleAtomPrinter;
import net.frodwith.jaque.exception.ExitException;

@ExportLibrary(InteropLibrary.class)
public final class InteropDebugDump implements TruffleObject {
  @TruffleBoundary
  public static void debugDump(Writer out, Object noun, boolean tail,
                                int depth, int maxDepth)
      throws IOException
  {
    if (depth >= maxDepth) {
      out.write("...");
      return;
    }

    if ( noun instanceof Cell ) {
      Cell c = (Cell) noun;
      if ( !tail ) {
        out.write('[');
      }
      debugDump(out, c.head, false, depth + 1, maxDepth);
      out.write(' ');
      debugDump(out, c.tail, true, depth + 1, maxDepth);

      if ( !tail ) {
        out.write(']');
      }
    }
    else {
      SimpleAtomPrinter.print(out, noun);
    }
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
      StringWriter w = new StringWriter();
      try {
        debugDump(w, arguments[0], false, 0, 35);
      }
      catch ( IOException e ) {}

      System.err.println(w.toString());

      return null;
    }
  }
}
