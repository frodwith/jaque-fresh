package net.frodwith.jaque.parser;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;

import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.Fail;

public final class CustomParser {

  private static abstract class Parsing {
    public final int startIndex;
    public int endIndex = -1;

    protected Parsing(int startIndex) {
      this.startIndex = startIndex;
    }

    public final void finalize(int endIndex) {
      this.endIndex = endIndex;
    }

    public final IndexLength indexLength() {
      assert( -1 != endIndex );
      return new IndexLength(startIndex, endIndex - startIndex);
    }

    public abstract Object write(Object axis, Map<Object,IndexLength> axisMap) throws Fail;
  }

  private static final class ParsingList extends Parsing {
    public final ArrayList<Parsing> children = new ArrayList<>();
    private ParsingAtom atom = null;

    public ParsingList(int startIndex) {
      super(startIndex);
    }

    public void parseDigit(int i, char c) {
      if ( null == atom ) {
        atom = new ParsingAtom(i);
      }
      atom.parseDigit(c);
    }

    public void finishAtom(int i) {
      if ( null != atom ) {
        atom.finalize(i);
        children.add(atom);
        atom = null;
      }
    }

    @Override
    public Object write(Object axis, Map<Object,IndexLength> axisMap) throws Fail {
      axisMap.put(axis, indexLength());
      int len = children.size() - 1, i = len;
      Object[] reversed = new Object[len];

      while ( i > 1 ) {
        reversed[--i] = children.remove(0).write(HoonMath.peg(axis, 2L), axisMap);
        axis          = HoonMath.peg(axis, 3L);
        int start     = children.get(0).startIndex;

        axisMap.put(axis, new IndexLength(start, endIndex - start));
      }

      Cell end = new Cell(
          children.get(0).write(HoonMath.peg(axis, 2L), axisMap),
          children.get(1).write(HoonMath.peg(axis, 3L), axisMap));

      while ( i < len ) {
        end = new Cell(reversed[i++], end);
      }
      
      return end;
    }
  }

  private static final class ParsingAtom extends Parsing {
    private final StringBuilder buf = new StringBuilder();

    public ParsingAtom(int startIndex) {
      super(startIndex);
    }

    public void parseDigit(char c) {
      buf.append(c);
    }

    @Override
    public Object write(Object axis, Map<Object,IndexLength> axisMap) {
      axisMap.put(axis, indexLength());
      return SimpleAtomParser.parse(buf);
    }
  }

  @TruffleBoundary
  public static SourceMappedNoun parse(SourceSection sourceSection) throws Fail {
    final CharSequence chars = sourceSection.getCharacters();
    final int len = chars.length();
    boolean fresh = true;
    ArrayDeque<ParsingList> stack = new ArrayDeque<>();
    stack.push(new ParsingList(0));

    for ( int i = 0; i < len; ++i ) {
      char c = chars.charAt(i);
      switch ( c ) {
        case '.':
          if ( fresh ) {
            throw new IllegalArgumentException(". outside atom at position " + i);
          }
          break;

        case '[':
          if ( !fresh ) {
            stack.peek().finishAtom(i);
            fresh = true;
          }
          stack.push(new ParsingList(i));
          break;

        case ']':
          if ( stack.size() < 2 ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          else {
            ParsingList top = stack.pop();
            if ( !fresh ) {
              top.finishAtom(i);
              fresh = true;
            }
            if ( top.children.size() < 2 ) {
              throw new IllegalArgumentException("[ with " +
                  top.children.size() + " elements beginning at position " +
                  top.startIndex);
            }
            top.finalize(i+1);
            stack.peek().children.add(top);
            break;
          }

        default:
          if ( Character.isSpace(c) ) {
            if ( !fresh ) {
              stack.peek().finishAtom(i);
              fresh = true;
            }
          }
          else if ( Character.isDigit(c) ) {
            stack.peek().parseDigit(i, c);
            fresh = false;
          }
          else {
            throw new IllegalArgumentException("unrecognized character " + 
                c + " at position " + i);
          }
      }
    }

    ParsingList top = stack.pop();
    if ( !fresh ) {
      top.finishAtom(len);
    }

    if ( 1 != top.children.size() ) {
      throw new IllegalArgumentException("multiple toplevel nouns");
    }

    if ( !stack.isEmpty() ) {
      throw new IllegalArgumentException("unclosed [ beginning at position " +
          stack.peek().startIndex);
    }

    Map<Object,IndexLength> axisMap = new HashMap<>();
    Object noun = top.children.remove(0).write(1L, axisMap);
    return new SourceMappedNoun(sourceSection, axisMap, noun);
  }
}
