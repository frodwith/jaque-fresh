package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import net.frodwith.jaque.data.Cell;

public class BasicNockTest {
  private Context context;

  @Before
  public void initEngine() {
    context = Context.create();
  }

  @Test
  public void testLiteralAtom() {
    Value  callable = context.eval("nock", "[1 42]");
    Number product  = callable.execute().as(Number.class);
    assertEquals(42L, product);
  }

  @Test
  public void testLiteralCell() {
    Value callable = context.eval("nock", "[1 0 42]");
    Value product  = callable.execute();
    assertEquals(0L, product.getMember("head").as(Number.class));
    assertEquals(42L, product.getMember("tail").as(Number.class));
  }

  @Test
  public void testFragment() {
    Value sam = context.eval("nock", "[0 6]");
    assertEquals(42L, sam.execute(new Cell(0L, new Cell(42L, 0L))));
  }

  @After
  public void dispose() {
    context.close();
  }
}
