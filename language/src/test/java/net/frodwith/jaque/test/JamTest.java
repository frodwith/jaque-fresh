package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static net.frodwith.jaque.parser.CustomParser.simple;

public class JamTest {
  @Test
  public void testExamples() {
    assertEquals(5456, HoonSerial.jam(42L), "jam 1");
    assertTrue(
      Equality.equals(
        SimpleAtomParser.parse("1.054.973.063.816.666.730.241"),
        HoonSerial.jam(
          new Cell(
            Cords.fromString("foo"),
            Cords.fromString("bar")))),
      "jam 2");
  }
}
