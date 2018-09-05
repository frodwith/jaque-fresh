package net.frodwith.jaque;

import java.util.ArrayList;
import java.util.ArrayDeque;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import gnu.math.MPN;

public class SimpleParser {

  public static Object parse(String src) {
    StringBuilder b = null;
    int i, len = src.length();
    ArrayList<Object> result = new ArrayList<Object>();
    ArrayDeque<ArrayList<Object>> s = new ArrayDeque<ArrayList<Object>>();
    s.push(result);

    for ( i = 0; i < len; ++i ) {
      char c = src.charAt(i);
      if ( Character.isDigit(c) ) {
        if ( null == b ) {
          b = new StringBuilder();
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
          s.peek().add(b.toString());
          b = null;
        }

        if ( c == '[' ) {
          s.push(new ArrayList<Object>());
        }
        else if ( c == ']' ) {
          if ( s.isEmpty() ) {
            throw new IllegalArgumentException("unbalanced ] at position " + i);
          }
          ArrayList<Object> fin = s.pop();
          if ( fin.size() < 2 ) {
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
  
  private static Object readRec(Object o) {
    if (o instanceof String) {
      return parseAtom((String) o);
    }
    else if (o instanceof ArrayList<?>) {
      ArrayList<Object> a = (ArrayList<Object>) o;
      int len = a.size();
      Object tail = readRec(a.remove(--len)),
             head = readRec(a.remove(--len));
      Cell end = new Cell(head, tail);

      while ( len-- > 0 ) {
        end = new Cell(readRec(a.remove(len)), end);
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
