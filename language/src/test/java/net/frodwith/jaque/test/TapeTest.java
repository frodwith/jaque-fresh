package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static net.frodwith.jaque.test.Util.assertNounEquals;

public class TapeTest {
  private static final Object helloTape = Lists.make(
      (long) 'h',
      (long) 'e',
      (long) 'l',
      (long) 'l',
      (long) 'o');

  @Test
  public void testTo() throws ExitException {
    assertEquals("hello", Tapes.toString(helloTape));
  }

  @Test
  public void testFrom() {
    assertNounEquals(helloTape, Tapes.fromString("hello"));
  }

  @Test
  public void testRunt() throws ExitException {
    assertEquals("gghello",
      Tapes.toString(Tapes.runt(2L, (long) 'g', helloTape)));
  }
}
