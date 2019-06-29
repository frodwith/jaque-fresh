package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import net.frodwith.jaque.util.AxisBuilder;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.parser.SimpleAtomParser;

public class AxisBuilderTest {
  @Test
  public void testSmall() {
    assertEquals(".", 1L, AxisBuilder.EMPTY.write());

    assertEquals("-", 2L, AxisBuilder.EMPTY.head().write());
    assertEquals("+", 3L, AxisBuilder.EMPTY.tail().write());

    assertEquals("-<", 4L, AxisBuilder.EMPTY.head().head().write());
    assertEquals("->", 5L, AxisBuilder.EMPTY.head().tail().write());

    assertEquals("+<", 6L, AxisBuilder.EMPTY.tail().head().write());
    assertEquals("+>", 7L, AxisBuilder.EMPTY.tail().tail().write());

    assertEquals("-<-", 8L, AxisBuilder.EMPTY.head().head().head().write());
    assertEquals("-<+", 9L, AxisBuilder.EMPTY.head().head().tail().write());
    assertEquals("->-", 10L, AxisBuilder.EMPTY.head().tail().head().write());
    assertEquals("->+", 11L, AxisBuilder.EMPTY.head().tail().tail().write());

    assertEquals("+<-", 12L, AxisBuilder.EMPTY.tail().head().head().write());
    assertEquals("+<+", 13L, AxisBuilder.EMPTY.tail().head().tail().write());
    assertEquals("+>-", 14L, AxisBuilder.EMPTY.tail().tail().head().write());
    assertEquals("+>+", 15L, AxisBuilder.EMPTY.tail().tail().tail().write());
  }

  @Test
  public void testLarge() {
    // 00011000 01010011 10110010 10111111 00000011 10010011 00110100 11100001 
    // 10101011 00011000 
    String lit = "11000" + "01010011" + "10110010" + "10111111" + "00000011" 
      + "10010011" + "00110100" + "11100001" + "10101011" + "00011000";
    Object parsed = SimpleAtomParser.parse(lit, 2);
    Object built = AxisBuilder.EMPTY.tail().head().head().head()
      .head().tail().head().tail().head().head().tail().tail()
      .tail().head().tail().tail().head().head().tail().head()
      .tail().head().tail().tail().tail().tail().tail().tail()
      .head().head().head().head().head().head().tail().tail()
      .tail().head().head().tail().head().head().tail().tail()
      .head().head().tail().tail().head().tail().head().head()
      .tail().tail().tail().head().head().head().head().tail()
      .tail().head().tail().head().tail().head().tail().tail()
      .head().head().head().tail().tail().head().head().head()
      .write();
    assertTrue("bigatom path", Equality.equals(parsed, built));
  }
}
