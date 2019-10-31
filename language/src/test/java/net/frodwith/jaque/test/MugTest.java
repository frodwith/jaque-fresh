package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Murmug;

public class MugTest {
  @Test
  public void testSmallAtom() {
    assertEquals(0x643849c6, Murmug.get(42L));
  }

  @Test
  public void testBigAtom() {
    BigAtom a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(0, a.cachedMug());
    assertEquals(0x601265fc, Murmug.get(a));
    assertNotEquals(0, a.cachedMug());
  }

  @Test
  public void testCell() {
    Cell a = new Cell(42L, 0L);
    assertEquals(0, a.cachedMug());
    assertEquals(0x5303a809, Murmug.get(a));
    assertNotEquals(0, a.cachedMug());
  }
}
