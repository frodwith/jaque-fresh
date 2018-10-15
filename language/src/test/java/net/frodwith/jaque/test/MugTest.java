package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Mug;

public class MugTest {
  @Test
  public void testSmallAtom() {
    assertEquals(0x2f0c9f3d, Mug.get(42L));
  }
  
  @Test
  public void testBigAtom() {
    BigAtom a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(0, a.mug);
    assertEquals(0x36d5e123, Mug.get(a));
    assertNotEquals(0, a.mug);
  }

  @Test
  public void testCell() {
    Cell a = new Cell(42L, 0L);
    assertEquals(0, a.cachedMug());
    assertEquals(0x6335a2a2, Mug.get(a));
    assertNotEquals(0, a.cachedMug());
  }
}
