package net.frodwith.jaque.parser;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.data.SourceMappedNoun.IndexLength;

import net.frodwith.jaque.exception.ExitException;

public final class CustomParser {

  private static final class ParsingResult {
    public final AxisMap<IndexLength> map;
    public final Object noun;

    public ParsingResult(AxisMap<IndexLength> map, Object noun) {
      this.map = map;
      this.noun = noun;
    }
  }

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

    public abstract ParsingResult finish() throws ExitException;
  }

  private static final class ParsingList extends Parsing {
    public final ArrayDeque<Parsing> children = new ArrayDeque<>();
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
        children.push(atom);
        atom = null;
      }
    }

    @Override
    public ParsingResult finish() throws ExitException {
      if ( children.size() < 2 ) {
        throw new ExitException("Cells must have at least 2 elements");
      }

      ParsingResult tail = children.pop().finish();

      while ( children.size() > 1 ) {
        Parsing p = children.pop();
        ParsingResult head = p.finish();
        int start = p.startIndex;
        IndexLength il = new IndexLength(start, endIndex - start);
        AxisMap<IndexLength> map = new AxisMap(il, head.map, tail.map);
        Cell noun = new Cell(head.noun, tail.noun);
        tail = new ParsingResult(map, noun);
      }

      ParsingResult head = children.pop().finish();
      AxisMap<IndexLength> map = new AxisMap(indexLength(), head.map, tail.map);
      Cell noun = new Cell(head.noun, tail.noun);
      return new ParsingResult(map, noun);
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
    public ParsingResult finish() {
      Object noun = SimpleAtomParser.parse(buf);
      AxisMap<IndexLength> map = AxisMap.single(indexLength());
      return new ParsingResult(map, noun);
    }
  }

  @TruffleBoundary
  public static SourceMappedNoun parse(SourceSection sourceSection)
    throws ExitException {
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
            stack.peek().children.push(top);
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

    ParsingResult r = top.children.pop().finish();

    return new SourceMappedNoun(sourceSection, r.map, r.noun);
  }

  public static Object simple(String str) throws ExitException {
    Source src = Source.newBuilder(NockLanguage.ID, str, "").build();
    return parse(src.createSection(0, str.length())).noun;
  }
}
