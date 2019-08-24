package net.frodwith.jaque.printer;

import java.io.Writer;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import gnu.math.MPN;

import net.frodwith.jaque.runtime.Atom;

public final class SimpleAtomPrinter {
  
  public static int print(Writer out, Object atom) throws IOException {
    return raw(out, Atom.words(atom), 10, 3);
  }

  @TruffleBoundary
  public static int raw(Writer out, int[] cur, int radix, int dot) throws IOException {
    ArrayDeque<Character> digits = new ArrayDeque<Character>();

    int len = cur.length,
        size = len,
        doc  = 0;

    if ( 0 == len ) {
      out.write('0');
      return 1;
    }

    cur = Arrays.copyOf(cur, len);

    while ( true ) {
      char dig = Character.forDigit(MPN.divmod_1(cur, cur, size, radix), radix);
      digits.push(dig);
      if (cur[len-1] == 0) {
        if (--len == 0) {
          break;
        }
      }
      if (++doc == dot) {
        doc = 0;
        digits.push('.');
      }
    }
    
    len = digits.size();
    while ( !digits.isEmpty() ) {
      out.write(digits.pop());
    }
    return len;
  }
}
