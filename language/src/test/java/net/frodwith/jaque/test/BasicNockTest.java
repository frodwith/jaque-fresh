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
    Number product  = callable.execute(0).as(Number.class);
    assertEquals(42, product);
  }

  @Test
  public void testLiteralCell() {
    Value callable = context.eval("nock", "[1 0 42]");
    Cell  product  = callable.execute(0).as(Cell.class);
    assertEquals(new Cell(0L, 42L), product);
  }

  @After
  public void dispose() {
    context.close();
  }
}
