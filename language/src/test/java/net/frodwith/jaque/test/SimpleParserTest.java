package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

import static net.frodwith.jaque.SimpleParser.parse;

public class SimpleParserTest {
  @Test
  public void testSmall() {
    assertEquals(42L, parse("42").toNoun());
  }
  
  @Test
  public void testBig() {
    Object a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(a, parse("78.896.609.586.032.353.644.659.982.063").toNoun());
  }

  @Test
  public void testCell() {
    Cell a = new Cell(0L, new Cell(1L, 2L));
    assertEquals(a, parse("[0 1 2]").toNoun());
  }
}
