package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Context;

public class JSTest {
  private Context context;

  @Before
  public void init() {
    context = Context.newBuilder()
      .allowAllAccess(true)
      .build();
  }

  @After
  public void dispose() {
    context.close();
  }

  @Test
  public void testAtom() {
    Value equal  = context.eval("nock", "[5 [0 1] 1 42]");
    Value jsNoun = context.eval("js", "Polyglot.import('nock').toNoun(42)");
    assertEquals(0L, equal.execute(jsNoun).as(Number.class));
  }

  @Test
  public void testCell() {
    Value equal  = context.eval("nock", "[5 [0 1] 1 1 2]");
    Value jsNoun = context.eval("js", "Polyglot.import('nock').toNoun(1, 2)");
    assertEquals(0L, equal.execute(jsNoun).as(Number.class));
  }

  @Test
  public void testNest() {
    Value equal  = context.eval("nock", "[5 [0 1] 1 1 [2 [3 4 5] 6] 7]");
    Value jsNoun = context.eval("js",
        "Polyglot.import('nock').toNoun(1, [2, [3, 4, 5], 6], 7)");
    assertEquals(0L, equal.execute(jsNoun).as(Number.class));
  }
}
