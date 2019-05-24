package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.library.NounLibrary;

public class MugTest {
  NounLibrary nouns = NounLibrary.getUncached();

  @Test
  public void testSmallAtom() {
    assertEquals(0x2f0c9f3d, nouns.mug(42L));
  }
  
  @Test
  public void testBigAtom() {
    BigAtom a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(0, a.cachedMug());
    assertEquals(0x36d5e123, nouns.mug(a));
    assertNotEquals(0, a.cachedMug());
  }

  @Test
  public void testCell() {
    Cell a = new Cell(42L, 0L);
    assertEquals(0, a.cachedMug());
    assertEquals(0x6335a2a2, nouns.mug(a));
    assertNotEquals(0, a.cachedMug());
  }
}
