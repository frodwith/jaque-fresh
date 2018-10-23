package net.frodwith.jaque.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import java.io.UnsupportedEncodingException;

public final class Cords {

  // Only UTF-8 strings, please.
  @TruffleBoundary
  private static byte[] stringBytes(String str) {
    try {
      return str.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException();
    }
  }

  @TruffleBoundary
  private static String bytesString(byte[] bytes) {
    try {
      return new String(bytes, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException();
    }
  }

  public static Object fromString(String str) {
    return Atom.fromByteArray(stringBytes(str));
  }

  public static String toString(Object cord) {
    return bytesString(Atom.toByteArray(cord));
  }
}
