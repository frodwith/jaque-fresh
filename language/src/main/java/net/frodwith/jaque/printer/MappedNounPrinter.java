package net.frodwith.jaque.printer;

import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;

// take a single noun, get a string and a location map
public final class MappedNounPrinter {
  public final Writer out;
  public final Map<Object,IndexLength> axisMap = new HashMap<>();

  private MappedNounPrinter(Writer out) {
    this.out = out;
  }

  public static Map<Object,IndexLength> print(Writer out, Object noun) throws IOException, Fail {
    MappedNounPrinter printer = new MappedNounPrinter(out);
    printer.print(noun, 1L, 0, false);
    return printer.axisMap;
  }

  // much simpler to print this recursively, and since it's only used for
  // debug info it's safe for now not to worry about stack overflows
  @TruffleBoundary
  private int print(Object noun, Object axis, int pos, boolean tail) throws IOException, Fail {
    int begin = pos;

    // for cells in tail position, position includes parent's closing ]
    boolean includeParentBracket = false;

    if ( noun instanceof Cell ) {
      Cell c = (Cell) noun;
      if ( !tail ) {
        out.write('[');
        ++pos;
      }
      pos = print(c.head, HoonMath.peg(axis, 2L), pos, false);
      out.write(' '); ++pos;
      pos = print(c.tail, HoonMath.peg(axis, 3L), pos, true);

      if ( tail ) {
        includeParentBracket = true;
      }
      else {
        out.write(']');
        ++pos;
      }
    }
    else {
      pos += SimpleAtomPrinter.print(out, noun);
    }

    axisMap.put(axis, new IndexLength(begin,
          (includeParentBracket ? pos+1 : pos) - begin));
    return pos;
  }
}
