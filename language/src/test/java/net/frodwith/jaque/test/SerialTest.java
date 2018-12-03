package net.frodwith.jaque.test;

import java.util.Random;

import org.junit.Test;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonSerial;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertTrue;
import static net.frodwith.jaque.parser.CustomParser.simple;

public class SerialTest {
  private static void aeq(String msg, Object a, Object b) {
    assertTrue(msg, Equality.equals(a, b));
  }

  private static Object c(String s) {
    return Cords.fromString(s);
  }

  private static Object randomAtom(Random r) {
    byte[] bytes = new byte[r.nextInt(8)];
    r.nextBytes(bytes);
    return Atom.fromByteArray(bytes);
  }

  private static Cell randomCell(Random r, int depth) {
    return new Cell(randomNoun(r, depth+1), randomNoun(r, depth+1));
  }

  private static Object randomNoun(Random r, int depth) {
    if ( depth < 10 && r.nextBoolean() ) {
      return randomCell(r, depth);
    }
    else {
      return randomAtom(r);
    }
  }

  @Test
  public void testExamples() throws ExitException {
    Object a = 5456L, b = 42L;
    aeq("jam 1", a, HoonSerial.jam(b));
    aeq("cue 1", b, HoonSerial.cue(a));
    a = simple("1.054.973.063.816.666.730.241");
    b = new Cell(c("foo"), c("bar"));
    aeq("jam 2", a, HoonSerial.jam(b));
    aeq("cue 2", b, HoonSerial.cue(a));
  }

  // QuickCheck would be better, but we can make do
  @Test
  public void testRandom() throws ExitException {
    Random r = new Random();
    for ( int i = 0; i < 100; ++i ) {
      Object noun = randomNoun(r, 0);
      aeq("random jam #" + i, noun, HoonSerial.cue(HoonSerial.jam(noun)));
    }
  }
}
