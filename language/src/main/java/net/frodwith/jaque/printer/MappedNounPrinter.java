package net.frodwith.jaque.printer;

import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;

// take a single noun, get a string and a location map
public final class MappedNounPrinter {
  public final Writer out;
  public AxisMap<IndexLength> axisMap = AxisMap.EMPTY;

  private MappedNounPrinter(Writer out) {
    this.out = out;
  }

  public static AxisMap<IndexLength> print(Writer out, Object noun)
    throws IOException, ExitException {
    MappedNounPrinter printer = new MappedNounPrinter(out);
    printer.print(noun, Axis.IDENTITY, 0, false);
    return printer.axisMap;
  }

  // much simpler to print this recursively, and since it's only used for
  // debug info it's safe for now not to worry about stack overflows
  @TruffleBoundary
  private int print(Object noun, Axis axis, int pos, boolean tail)
    throws IOException, ExitException {
    int begin = pos;

    // for cells in tail position, position includes parent's closing ]
    boolean includeParentBracket = false;

    if ( noun instanceof Cell ) {
      Cell c = (Cell) noun;
      if ( !tail ) {
        out.write('[');
        ++pos;
      }
      pos = print(c.head, axis.peg(2), pos, false);
      out.write(' '); ++pos;
      pos = print(c.tail, axis.peg(3), pos, true);

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

    axisMap = axisMap.insert(axis, 
        new IndexLength(begin, (includeParentBracket ? pos+1 : pos) - begin));
    return pos;
  }
}
