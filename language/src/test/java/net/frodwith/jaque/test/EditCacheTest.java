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
=<  [zero one]
=>  =>  %root42
    ~%  %root42  ~  ~
    |%
    ++  version  42
    --
~%  %one  +  ~
|%
++  zero
  ~/  %zero
  |=  a=$-(* *)
  a(+< ~)
++  one
  ~<  %core
  |=  a=@
  +(a)
--
*/

public class EditCacheTest {
  private static final String ZERO_SOURCE_STRING = "[7 [7 [7 [1 55.200.873.148.274] 8 [1 1 42] 11 [1.953.718.630 1 55.200.873.148.274 [1 0] 0] 0 1] 8 [1 [7 [8 [1 [0 15] 0 0 0] [1 10 [6 1 0] 0 6] 0 1] 11 [1.953.718.630 1 1.869.768.058 [0 7] 0] 0 1] 7 [8 [1 0] [1 4 0 6] 0 1] 11 1.701.998.435 0 1] 11 [1.953.718.630 1 6.647.407 [0 3] 0] 0 1] [9 4 0 1] 9 5 0 1]";
  private static final Source zeroSource =
    Source.newBuilder("nock", ZERO_SOURCE_STRING, "zero.nock").buildLiteral();

  private Context context;

  @Before
  public void initEngine() {
    context = Context.create();
  }

  @Test
  public void testCache() {
    Value pair = context.eval(zeroSource).execute();
    Value zero = pair.getArrayElement(0);
    Value one = pair.getArrayElement(1);

    // will know it's a core because it was fast hinted
    assertTrue(zero.getMetaObject().getMember("isCore").as(Boolean.class));

    Value mzero = zero.getMetaObject().invokeMember("2", zero);
    assertEquals(0L, mzero.getArrayElement(1).getArrayElement(0).as(Number.class));
    // edited outside a location relevant axis, so still a core
    assertTrue(mzero.getMetaObject().getMember("isCore").as(Boolean.class));

    // will know it's a core because it was core hinted
    assertTrue(one.getMetaObject().getMember("isCore").as(Boolean.class));
    Value mone = context.eval("nock", "[10 [6 1 42] 0 1]").execute(one);

    // edited outside a location relevant axis, so still a core
    assertTrue(mone.getMetaObject().getMember("isCore").as(Boolean.class));
    assertEquals(43L, mone.getMetaObject().invokeMember("2").as(Number.class));

    // still a core after we invoked it
    assertTrue(mone.getMetaObject().getMember("isCore").as(Boolean.class));
  }

  @After
  public void dispose() {
    context.close();
  }
}
