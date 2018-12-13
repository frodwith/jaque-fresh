package net.frodwith.jaque.test;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Context;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;

public class MessageTest {
  Context context;

  @Before
  public void init() {
    context = Context.create();
  }

  @After
  public void dispose() {
    context.close();
  }

  @Test
  public void testExec() {
    exId(context.eval("nock", "[0 1]"));
  }

  @Test
  public void testMetaExec() {
    exId(context.eval("nock", "[1 0 1]").execute().getMetaObject());
  }

  @Test
  public void testInvoke() {
    Value core = context.eval("nock", "[1 [0 6] 42 0]")
                 .execute().getMetaObject();
    assertEquals(42L, core.invokeMember("2").as(Number.class));
    assertEquals(1L, core.invokeMember("2", 1L).as(Number.class));

    kelp(core.invokeMember("2", 42, 43));
  }

  private void kelp(Value cell) {
    assertEquals(42L, cell.getMember("head").as(Number.class));
    assertEquals(43L, cell.getMember("tail").as(Number.class));
  }

  private void exId(Value fn) {
    assertEquals(0L, fn.execute().as(Number.class));
    assertEquals(42L, fn.execute(42L).as(Number.class));
    
    kelp(fn.execute(42L, 43L));
  }
}
