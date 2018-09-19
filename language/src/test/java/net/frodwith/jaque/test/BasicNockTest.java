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
    Cell data = new Cell(1L, new Cell(2L, 3L));

    assertEquals("-", 1L, context.eval("nock", "[0 2]").execute(data).as(Number.class));
    assertEquals("+<", 2L, context.eval("nock", "[0 6]").execute(data).as(Number.class));
    assertEquals("+>", 3L, context.eval("nock", "[0 7]").execute(data).as(Number.class));

    Value tail = context.eval("nock", "[0 3]").execute(data);
    assertEquals("+memhead", 2L, tail.getMember("head").as(Number.class));
    assertEquals("+memtail", 3L, tail.getMember("tail").as(Number.class));
  }

  @Test
  public void testNock() {
    Value simple = context.eval("nock", "[2 [0 3] 0 2]");
    Cell subFormula = new Cell(0L, 2L);
    Cell subSubject = new Cell(42L, 0L);
    Cell subject    = new Cell(subFormula, subSubject);
    assertEquals("simple 2", 42L, simple.execute(subject).as(Number.class));
  }

  @After
  public void dispose() {
    context.close();
  }
}
