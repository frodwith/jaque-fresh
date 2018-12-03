package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonSerial;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static net.frodwith.jaque.parser.CustomParser.simple;

public class JamTest {
  @Test
  public void testExamples() throws ExitException {
    assertEquals("jam 1", 5456L, HoonSerial.jam(42L));
    assertTrue("jam 2", 
      Equality.equals(
        simple("1.054.973.063.816.666.730.241"),
        HoonSerial.jam(
          new Cell(
            Cords.fromString("foo"),
            Cords.fromString("bar")))));
  }
}
