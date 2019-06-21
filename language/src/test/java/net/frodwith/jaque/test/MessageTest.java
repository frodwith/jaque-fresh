package net.frodwith.jaque.test;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Context;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;

public class MessageTest {
  Context context;
  Value id, head, tail;

  @Before
  public void init() {
    context = Context.create();
    id   = context.eval("nock", "[0 1]");
    head = context.eval("nock", "[0 2]");
    tail = context.eval("nock", "[0 3]");
  }

  @After
  public void dispose() {
    context.close();
  }

  private static void assertInt(int expected, Value value) {
    assertEquals(expected, (int) value.as(int.class));
  }

  // this class used to test a richer interop api for cells, which has been
  // discarded in favor of using nock exclusively to break apart nouns
  // the new patterns are tested here (partly as an example).
  @Test
  public void testNoArg() {
    assertInt(0, context.eval("nock", "[0 1]").execute());
  }

  @Test
  public void testCellArg() {
    Value r = id.execute(42, 43);
    assertInt(42, head.execute(r));
    assertInt(43, tail.execute(r));
  }

  @Test
  public void testTrelArg() {
    Value r = id.execute(42, 43, 44);
    assertInt(42, context.eval("nock", "[0 2]").execute(r));
    assertInt(43, context.eval("nock", "[0 6]").execute(r));
    assertInt(44, context.eval("nock", "[0 7]").execute(r));
  }

  @Test
  public void testNock() {
    Value formula = context.eval("nock", "[1 0 1]").execute(),
          cell    = context.eval("nock", "[1 42 43]").execute(),
          nock    = context.eval("nock", "[2 [0 3] 0 2]"),
          r       = nock.execute(0, cell);
    assertInt(0, nock.execute(0, formula));
    assertInt(42, head.execute(r));
    assertInt(43, tail.execute(r));
  }

  @Test
  public void testKick() {
    Value gate = context.eval("nock", "[1 [0 6] 42 0]").execute(),
          bunt = context.eval("nock", "[9 2 0 1]"),
          slam = context.eval("nock", "[9 2 10 [6 0 3] 0 2]"),
          r    = slam.execute(gate, 42, 43);

    assertInt(42, bunt.execute(gate));
    assertInt(1, slam.execute(gate, 1));

    assertInt(42, head.execute(r));
    assertInt(43, tail.execute(r));
  }
}
