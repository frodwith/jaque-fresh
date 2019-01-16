package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.runtime.Tanks;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static net.frodwith.jaque.test.Util.assertNounEquals;

public class TankTest {

  private static Object wall(String... strings) {
    Object[] tapes = new Object[strings.length];
    for ( int i = 0; i < strings.length; ++i ) {
      tapes[i] = Tapes.fromString(strings[i]);
    }
    return Lists.make(tapes);
  }

  private static Object leaf(String s) {
    return new Cell((long) Motes.LEAF, Tapes.fromString(s));
  }

  private static Object rose(String space, String left, String right,
      Object... children) {
    return new Trel((long) Motes.ROSE,
      new Trel(Tapes.fromString(space),
               Tapes.fromString(left),
               Tapes.fromString(right)).toNoun(),
      Lists.make(children)).toNoun();
  }

  private static Object palm(String wideSeperator, String caption,
                             String widePrefix, String wideSuffix,
    Object... children) {
    return new Trel((long) Motes.PALM,
      new Qual(Tapes.fromString(wideSeperator),
               Tapes.fromString(caption),
               Tapes.fromString(widePrefix),
               Tapes.fromString(wideSuffix)).toNoun(),
      Lists.make(children)).toNoun();
  }

  @Test
  public void testLeaf() throws ExitException {
    Object tank = leaf("well, hello there!");

    assertNounEquals(Tanks.wash(2L, 80L, tank), wall("  well, hello there!"));
    assertNounEquals(Tanks.wash(4L, 20L, tank), wall(
      "    \\/well, hello \\/",
      "      there!",
      "    \\/            \\/"));
  }

  @Test
  public void testRose() throws ExitException {
    Object tank = rose(" ", "[", "]",
      leaf("well,"),
      leaf("hello"),
      leaf("there!"));

    assertNounEquals(Tanks.wash(2L, 80L, tank),
                     wall("  [well, hello there!]"));

    assertNounEquals(Tanks.wash(4L, 20L, tank), wall(
      "    [ well,",
      "      hello",
      "      there!",
      "    ]"));
  }

  @Test
  public void testPalm() throws ExitException {
    Object tank = palm(" ", "obi: ", ":D ", " D:",
      leaf("well,"),
      leaf("hello"),
      leaf("there!"));

    assertNounEquals(Tanks.wash(0L, 80L, tank),
                     wall("obi: :D well, hello there! D:"));

    assertNounEquals(Tanks.wash(4L, 20L, tank), wall(
      "    obi: ",
      "        well,",
      "      hello",
      "    there!"));
  }
}
