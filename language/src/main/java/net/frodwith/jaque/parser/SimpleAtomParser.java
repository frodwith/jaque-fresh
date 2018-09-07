package net.frodwith.jaque.parser;

import gnu.math.MPN;

import net.frodwith.jaque.runtime.Atom;

public final class SimpleAtomParser {
  public static Object parse(CharSequence s) {
    return parse(s, 10);
  }
  
  public static Object parse(CharSequence s, int radix) {
    int    len = s.length(),
           cpw = MPN.chars_per_word(radix),
           i;
    byte[] dig = new byte[len];
    int[]  wor = new int[(len / cpw) + 1];

    for (i = 0; i < len; ++i) {
        dig[i] = (byte) Character.digit(s.charAt(i), radix);
    }

    MPN.set_str(wor, dig, len, radix);

    return Atom.malt(wor);
  }
}
