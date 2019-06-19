package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class ValueSubjectTest {
  private Context a, b;

  @Before
  public void initEngine() {
    a = Context.create();
    b = Context.create();
  }

  @After
  public void dispose() {
    a.close();
    b.close();
  }

  @Test
  public void testSameContext() {
    Value triple = a.eval("nock", "[1 1 2 3]").execute(),
          third = a.eval("nock", "[0 7]").execute(triple);
    assertEquals(3L, third.as(Number.class));
  }

  @Test
  public void testCrossContext() {
    Value triple = a.eval("nock", "[1 1 2 3]").execute(),
          third;
    Exception thrown = null;
    try {
      third = b.eval("nock", "[0 7]").execute(triple);
    }
    catch (Exception e) {
      thrown = e;
    }
    assertNotNull("values cannot be shared across contexts", thrown);
  }
}
