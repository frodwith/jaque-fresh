package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;

/*
!=
=<  zero
=>  =>  %root42
    ~%  %root42  ~  ~
    |%
    ++  version  42
    --
~%  %one  +  ~
|%
++  zero
  ~/  %zero
  |=  a/$-(* *)
  a(+< ~)
--
*/

public class EditCacheTest {
  private static final String ZERO_SOURCE_STRING = "[7 [7 [7 [1 55.200.873.148.274] 8 [1 1 42] 11 [1.953.718.630 1 55.200.873.148.274 [1 0] 0] 0 1] 8 [1 7 [8 [1 [0 15] 0 0 0] [1 10 [6 1 0] 0 6] 0 1] 11 [1.953.718.630 1 1.869.768.058 [0 7] 0] 0 1] 11 [1.953.718.630 1 6.647.407 [0 3] 0] 0 1] 9 2 0 1]";
  private static final Source zeroSource =
    Source.newBuilder("nock", ZERO_SOURCE_STRING, "zero.nock").buildLiteral();

  private Context context;

  @Before
  public void initEngine() {
    context = Context.create();
  }

  @Test
  public void testCache() {
    Value gate = context.eval(zeroSource).execute();
    // will know it's a core because it was fast hinted
    assertTrue(gate.getMetaObject().getMember("isCore").as(Boolean.class));

    Value mute = gate.getMetaObject().invokeMember("2", gate);
    assertEquals(0L, mute.getMember("tail").getMember("head").as(Number.class));
    // edited outside a location relevant axis, so still a core
    assertTrue(mute.getMetaObject().getMember("isCore").as(Boolean.class));
  }

  @After
  public void dispose() {
    context.close();
  }
}
