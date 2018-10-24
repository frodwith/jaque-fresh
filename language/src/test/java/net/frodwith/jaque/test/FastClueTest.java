package net.frodwith.jaque.test;

import org.junit.Test;

import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertEquals;
import static net.frodwith.jaque.parser.CustomParser.simple;

public class FastClueTest {
  @Test
  public void testBasic() throws ExitException {
    Object noHookGate = simple("[1.702.125.927 [0 7] 0]");
    FastClue clue = FastClue.parse(noHookGate);
    assertEquals("gate", clue.name);
    assertEquals(7L, (long) clue.toParent.atom);
    assertEquals(0, clue.hooks.size());
  }

  @Test
  public void testElaborate() throws ExitException {
    Object hookedCore = simple("[1.701.998.435 [0 3] " +
        "[7.496.054 0 6] " +
        "[7.303.014 9 4 0 1] " +
        "[7.496.034 9 5 0 1] 0]");
    FastClue clue = FastClue.parse(hookedCore);
    assertEquals("core", clue.name);
    assertEquals(3L, (long) clue.toParent.atom);
    assertEquals(3, clue.hooks.size());
    assertEquals(6L, ((FastClue.FragHook) clue.hooks.get("var")).axis.atom);

    FastClue.PullHook hook = (FastClue.PullHook) clue.hooks.get("foo");
    assertEquals(4L, hook.arm.atom);
    assertEquals(1L, hook.toSubject.atom);

    hook = (FastClue.PullHook) clue.hooks.get("bar");
    assertEquals(5L, hook.arm.atom);
    assertEquals(1L, hook.toSubject.atom);
  }
}
