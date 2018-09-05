package net.frodwith.jaque.parser;

import java.util.ArrayList;
import java.util.ArrayDeque;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import gnu.math.MPN;

public final class CustomParser {

  public static ParsedNoun parse(String src) {
    StringBuilder b = null;
    int i, len = src.length();
    int atomStart = -1;

    ArrayList<Object> result = new ArrayList<Object>();
    ArrayDeque<ArrayList<Object>> s = new ArrayDeque<ArrayList<Object>>();
    s.push(result);

    for ( i = 0; i < len; ++i ) {
      char c = src.charAt(i);
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
          s.peek().add(new ParsedAtom(parseAtom(b.toString()), atomStart, i - atomStart));
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
      return readRec(tree.get(0));
    }
  }

  // TODO, pretty easy: parser test! get positions right and shit!
  
  private static ParsedNoun readRec(Object o) {
    if (o instanceof ParsedAtom) {
      return (ParsedAtom) o;
    }
    else if (o instanceof ArrayList<?>) {
      ArrayList<Object> a = (ArrayList<Object>) o;
      int startPos = a.remove(0);
      int endPos   = a.remove(0);
      int len = a.size();
      ParsedNoun tail = readRec(a.remove(--len)),
                 head = readRec(a.remove(--len));

      ParsedCell end = new ParsedCell(head, tail, 
          head.position, endPos - head.position);

      while ( len-- > 0 ) {
        ParsedNoun mor = readRec(a.remove(len));
        end = new ParsedCell(mor, end, 
            mor.position, endPos - head.position);
      }

      return end;
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  public static Object parseAtom(String s) {
    return parseAtom(s, 10);
  }

  public static Object parseAtom(String s, int radix) {
    return parseAtom(s.toCharArray(), radix);
  }

  public static Object parseAtom(char[] car, int radix) {
    int    len = car.length,
           cpw = MPN.chars_per_word(radix),
           i;
    byte[] dig = new byte[len];
    int[]  wor = new int[(len / cpw) + 1];

    for (i = 0; i < len; ++i) {
        dig[i] = (byte) Character.digit(car[i], radix);
    }

    MPN.set_str(wor, dig, len, radix);

    return Atom.malt(wor);
  }
}
