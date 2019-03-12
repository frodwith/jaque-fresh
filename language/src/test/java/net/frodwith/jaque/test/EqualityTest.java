package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Equality;

public class EqualityTest {
  @Test
  public void testAtoms() {
    Object a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef }),
           b = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef }),
           c = new BigAtom(new int[] { 0xdeadbeed, 0xbeefdead, 0xfeedbeef });

    assertTrue(Equality.equals(42L, 42L));
    assertEquals(".equals()", a, b);
    assertTrue(Equality.equals(a, b));
    assertFalse(Equality.equals(b, c));
  }

  @Test
  public void testDeepEquals() {
    Cell a = new Cell(42L, new Cell(43L, 45L)),
         b = new Cell(42L, new Cell(43L, 45L)),
         c = new Cell(42L, new Cell(44L, 45L));

    assertEquals(".equals()", a, b);
    assertTrue(Equality.equals(a, b));
    assertFalse(Equality.equals(b, c));
  }

  @Test
  public void testUnifyCells() {
    Cell a = new Cell(42L, new Cell(43L, 45L)),
         b = new Cell(42L, new Cell(43L, 45L));

    a.mug();
    assertFalse("!=tail", a.tail == b.tail);
    assertNotEquals("!=mug", a.cachedMug(), b.cachedMug());
    assertTrue(Equality.equals(a, b));
    assertTrue("=tail", a.tail == b.tail);
    assertEquals("=mug", a.cachedMug(), b.cachedMug());
  }

  @Test
  public void testUnifyAtoms() {
    BigAtom a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef }),
            b = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });

    a.hashCode();
    assertNotEquals("!=words", a.words, b.words);
    assertNotEquals("!=mug", a.cachedMug(), b.cachedMug());
    assertTrue(Equality.equals(a, b));
    assertEquals("=words", a.words, b.words);
    assertEquals("=mug", a.cachedMug(), b.cachedMug());
  }

  @Test
  public void testBugOne() {
    Cell a = new Cell(40L, new Cell(new Cell(0L, 42L), 1042L)),
         b = new Cell(40L, new Cell(new Cell(0L, 42L), 1042L));

    assertTrue(Equality.equals(a, b));
  }
}
