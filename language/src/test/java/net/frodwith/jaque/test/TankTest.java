package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.runtime.Tanks;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static net.frodwith.jaque.test.Util.assertNounEquals;

public class TankTest {

  private static Object leaf(String s) {
    return new Cell((long) Motes.LEAF, Tapes.fromString(s));
  }

  private static Object rose(String space, String left, String right,
      Object... children) {
    return new Trel(
      (long) Motes.ROSE,
      new Trel(Tapes.fromString(space),
               Tapes.fromString(left),
               Tapes.fromString(right)).toNoun(),
      Lists.make(children)).toNoun();
  }

  @Test
  public void testLeaf() throws ExitException {
    Object tank = leaf("well, hello there!");

    assertNounEquals(Lists.make(Tapes.fromString("  well, hello there!")),
        Tanks.wash(2L, 80L, tank));

    Object wall = Lists.make(
      Tapes.fromString("    \\/well, hello \\/"),
      Tapes.fromString("      there!"),
      Tapes.fromString("    \\/            \\/"));
    assertNounEquals(wall, Tanks.wash(4L, 20L, tank));
  }

  @Test
  public void testRose() throws ExitException {
    Object tank = rose(" ", "[", "]",
      leaf("well,"),
      leaf("hello"),
      leaf("there!"));

    assertNounEquals(
      Lists.make(Tapes.fromString("  [well, hello there!]")),
      Tanks.wash(2L, 80L, tank));

    Object wall = Lists.make(
      Tapes.fromString("    [ well,"),
      Tapes.fromString("      hello"),
      Tapes.fromString("      there!"),
      Tapes.fromString("    ]"));
    assertNounEquals(wall, Tanks.wash(4L, 20L, tank));
  }

  /*
  @Test
  public void testPalm() throws ExitException {
  }
  */
}
