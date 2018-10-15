package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Mug;

public class AxisTest {
  @Test
  public void testSubAxis() {
    assertTrue(subAxis(7L, 3L));
    assertFalse(subAxis(7L, 2L));
    assertFalse(subAxis(3L, 7L));

    assertTrue(subAxis(12L, 3L));
    assertFalse(subAxis(12L, 2L));
    assertFalse(subAxis(3L, 12L));

    assertTrue(subAxis(12L, 6L));
    assertFalse(subAxis(12L, 7L));
    assertFalse(subAxis(6L, 12L));

    assertTrue(subAxis(9L, 2L));
    assertFalse(subAxis(9L, 3L));
    assertFalse(subAxis(9L, 5L));
    assertFalse(subAxis(2L, 9L));

    assertTrue(subAxis(3L, 3L));
  }
}
