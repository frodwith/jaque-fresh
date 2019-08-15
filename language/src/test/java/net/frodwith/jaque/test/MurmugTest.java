package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.runtime.Murmug;

public class MurmugTest {
  @Test
  public void testStrings() {
    assertEquals(0x4d441035, Murmug.get(Cords.fromString("Hello, world!")));
    assertEquals(0x64dfda5c,
      Murmug.get(Cords.fromString("xxxxxxxxxxxxxxxxxxxxxxxxxxxx")));
  }

  @Test
  public void testSmallAtom() {
    assertEquals(0x643849c6, Murmug.get(42L));
    assertEquals(0x79ff04e8, Murmug.get(0L));
  }

  @Test
  public void testBigAtom() {
    BigAtom a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(0x601265fc, Murmug.get(a));

    a = (BigAtom)HoonMath.lsh((byte)3, 1,
      HoonMath.add(new BigAtom((int[])HoonMath.bex(212L)),
                   Cords.fromString("abcdefjhijklmnopqrstuvwxyz"))
    );
    assertEquals(0x34d08717, Murmug.get(a));
  }

  @Test
  public void testCell() {
    Cell a = new Cell(42L, 0L);
    assertEquals(0x5303a809, Murmug.get(a));

    a = new Cell(0L, 0L);
    assertEquals(0x389ca03a, Murmug.get(a));

    a = new Cell(1L, 1L);
    assertEquals(0x389ca03a, Murmug.get(a));

    a = new Cell(0L, HoonMath.bex(32L));
    assertEquals(0x5258a6c0, Murmug.get(a));

    a = new Cell(HoonMath.dec(new BigAtom((int[])HoonMath.bex(128L))), 1L);
    assertEquals(0x2ad39968, Murmug.get(a));
  }
}
