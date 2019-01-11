package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.runtime.Tanks;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static net.frodwith.jaque.test.Util.assertNounEquals;

public class TankTest {

  @Test
  public void testLeaf() throws ExitException {
    Object tank = new Cell((long) Motes.LEAF,
        Tapes.fromString("well, hello there!"));

    assertNounEquals(Lists.make(Tapes.fromString("  well, hello there!")),
        Tanks.wash(2L, 80L, tank));

    Object wall = Lists.make(
          Tapes.fromString("    \\/well, hello \\/"),
          Tapes.fromString("      there!"),
          Tapes.fromString("    \\/            \\/"));
    assertNounEquals(wall, Tanks.wash(4L, 20L, tank));
  }

  /*
  @Test
  public void testRose() {
  }

  @Test
  public void testPalm() throws ExitException {
  }
  */
}
