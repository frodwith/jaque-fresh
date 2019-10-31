package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

import static net.frodwith.jaque.data.Axis.subAxis;

public class AxisTest {
  @Test
  public void testSubAxis() {
    assertTrue("7.3", subAxis(7L, 3L));
    assertFalse("7.2", subAxis(7L, 2L));
    assertFalse("3.7", subAxis(3L, 7L));

    assertTrue("12.3", subAxis(12L, 3L));
    assertFalse("12.2", subAxis(12L, 2L));
    assertFalse("3.12", subAxis(3L, 12L));

    assertTrue("12.6", subAxis(12L, 6L));
    assertFalse("12.7", subAxis(12L, 7L));
    assertFalse("6.12", subAxis(6L, 12L));

    assertTrue("9.2", subAxis(9L, 2L));
    assertFalse("9.3", subAxis(9L, 3L));
    assertFalse("9.5", subAxis(9L, 5L));
    assertFalse("2.9", subAxis(2L, 9L));

    assertTrue("same", subAxis(3L, 3L));
  }
}
