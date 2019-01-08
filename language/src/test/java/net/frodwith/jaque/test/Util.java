package net.frodwith.jaque.test;

import net.frodwith.jaque.runtime.Equality;

import static org.junit.Assert.assertTrue;

public final class Util {
  public final static void assertNounEquals(String message, Object a, Object b) {
    assertTrue(message, Equality.equals(a, b));
  }

  public final static void assertNounEquals(Object a, Object b) {
    assertTrue(Equality.equals(a, b));
  }
}
