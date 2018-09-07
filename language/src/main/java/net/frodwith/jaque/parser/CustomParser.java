package net.frodwith.jaque.parser;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import gnu.math.MPN;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.FailException;

public final class CustomParser {

  public static final class ParsedAtom {
    public final Object atom;
    public final int index;
    public final int length;

    public ParsedAtom(Object atom, int index, int length) {
      this.atom = atom;
      this.index = index;
      this.length = length;
    }
  }

  final Map<Object,IndexLength> axisMap;

  private CustomParser() {
    this.axisMap = new HashMap<>();
  }

  @TruffleBoundary
  public static SourceMappedNoun parse(SourceSection sourceSection) {
    StringBuilder b = null;
    CharSequence chars = sourceSection.getCharacters();
    int i, len = chars.length();
    int atomStart = -1;

    ArrayList<Object> result = new ArrayList<Object>();
    ArrayDeque<ArrayList<Object>> s = new ArrayDeque<ArrayList<Object>>();
    s.push(result);

    for ( i = 0; i < len; ++i ) {
      char c = chars.charAt(i);
      if ( Character.isDigit(c) ) {
        if ( null == b ) {
          assert(atomStart == -1);
          b = new StringBuilder();
          atomStart = i;
        }
        b.append(c);
      }
      else if ( c == '.' ) {
        if ( null == b ) {
          throw new IllegalArgumentException(". outside atom at position " + i);
        }
      }
      else {

        if ( null != b ) {
          Object atom = SimpleAtomParser.parse(b);
          s.peek().add(new ParsedAtom(atom, atomStart, i - atomStart));
          b = null;
          atomStart = -1;
        }

        if ( c == '[' ) {
          ArrayList<Object> a = new ArrayList<Object>();
          a.add(i);
          s.push(a);
        }
        else if ( c == ']' ) {
          if ( s.isEmpty() ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          ArrayList<Object> fin = s.pop();
          fin.add(1, i); // [startPos endPos e1 e2 e3...]
          if ( fin.size() < 4 ) {
            throw new IllegalArgumentException("cell with less than 2 elements at position" + i);
          }
          if ( s.isEmpty() ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          s.peek().add(fin);
        }
        else if ( Character.isSpaceChar(c) ) {
          continue;
        }
        else {
          throw new IllegalArgumentException("unrecognized character " + c + " at position " + i);
        }
      }
    }

    if ( null != b ) {
      s.peek().add(b.toString());
    }

    if ( s.isEmpty() ) {
      throw new IllegalArgumentException("too many ]");
    }
    else {
      ArrayList<Object> tree = s.pop();
      if ( !s.isEmpty() ) {
        throw new IllegalArgumentException("missing ]");
      }
      if ( 1 != tree.size() ) {
        throw new IllegalArgumentException("multiple nouns not surrounded by brackets");
      }
      CustomParser parser = new CustomParser();
      Object noun = parser.readRec(tree.get(0), 1L);
      return new SourceMappedNoun(sourceSection, parser.axisMap, noun);
    }
  }

  private Object readRec(Object o, Object axis) {
    if (o instanceof ParsedAtom) {
      ParsedAtom parsedAtom = (ParsedAtom) o;
      axisMap.put(axis, new IndexLength(parsedAtom.index, parsedAtom.length));
      return parsedAtom.atom;
    }
    else if (o instanceof ArrayList<?>) {
      ArrayList<Object> a = (ArrayList<Object>) o;
      int startPos = (int) a.remove(0);
      int endPos   = (int) a.remove(0);
      int len      = a.size();

      // [2   3]
      // [2  6  7]
      // [2 6 14  15]
      // [2 6 14 30 31]
      Object top, riteAx, leftAx;

      try {
        top    = HoonMath.lsh((byte) 0, a.size(), 1L);
        riteAx = HoonMath.dec(top);
        leftAx = HoonMath.dec(riteAx);

        Object tail = readRec(a.remove(--len), HoonMath.peg(axis, riteAx)),
               head = readRec(a.remove(--len), HoonMath.peg(axis, leftAx));

        Cell end = new Cell(head, tail);

        while ( len-- > 0 ) {
          riteAx = HoonMath.rsh((byte) 0, 1, leftAx);
          leftAx = HoonMath.dec(riteAx);

          Object mor = readRec(a.remove(len), leftAx);
          end = new Cell(mor, end);
        }

        axisMap.put(axis, new IndexLength(startPos, endPos - startPos));
        return end;
      }
      catch ( FailException e) {
        throw new RuntimeException("readRec internal fail");
      }
    }
    else {
      throw new IllegalArgumentException();
    }
  }
}
