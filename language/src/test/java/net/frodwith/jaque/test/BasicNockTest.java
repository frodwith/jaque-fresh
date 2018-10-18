package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.PolyglotException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.parser.SimpleAtomParser;
import net.frodwith.jaque.exception.Bail;

public class BasicNockTest {
  private Context context;

  @Before
  public void initEngine() {
    context = Context.create();
  }

  @Test
  public void testAutocons() {
    Value flipper = context.eval("nock", "[[0 3] 0 2]");
    Value product = flipper.execute(new Cell(42L, 0L));
    assertEquals(0L, product.getMember("head").as(Number.class));
    assertEquals(42L, product.getMember("tail").as(Number.class));
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
    assertEquals(42L, simple.execute(subject).as(Number.class));
  }

  @Test
  public void testDeep() {
    Value test = context.eval("nock", "[3 0 1]");
    assertEquals("@", 1L, test.execute(3L).as(Number.class));
    assertEquals("^", 0L, test.execute(new Cell(0L, 0L)).as(Number.class));
  }

  @Test
  public void testBump() {
    Value bump = context.eval("nock", "[4 0 1]");
    assertEquals(1L, bump.execute(0L).as(Number.class));
    assertEquals(42L, bump.execute(41L).as(Number.class));

    Object big = SimpleAtomParser.parse("1.000.000.000.000.000.000.000.000");
    BigAtom bigger = (BigAtom) SimpleAtomParser.parse("1.000.000.000.000.000.000.000.001");
    int[] got = bump.execute(big).as(int[].class);
    assertArrayEquals("huge", bigger.words, got);

    got = bump.execute(0xFFFFFFFFFFFFFFFFL).as(int[].class);
    assertArrayEquals("max", new int[] {0,0,1}, got);
  }

  @Test
  public void testSame() {
    Value same = context.eval("nock", "[5 0 1]");
    assertEquals(0L, same.execute(new Cell(42L, 42L)).as(Number.class));
    assertEquals(1L, same.execute(new Cell(42L, 43L)).as(Number.class));
    
    assertEquals(0L, context.eval("nock", "[5 [1 42] 1 42]")
                       .execute()
                       .as(Number.class));
  }

  @Test
  public void testIf() {
    Value test = context.eval("nock", "[6 [0 1] [1 40] 1 2]");
    assertEquals(40L, test.execute(0L).as(Number.class));
    assertEquals(2L, test.execute(1L).as(Number.class));
    PolyglotException two = null, cell = null;

    try {
      test.execute(2L);
    }
    catch ( PolyglotException e ) {
      two = e;
    }
    assertNotNull(two);
    assertTrue(two.isGuestException());

    try {
      test.execute(new Cell(0L, 0L));
    }
    catch ( PolyglotException e ) {
      cell = e;
    }
    assertNotNull(cell);
    assertTrue(cell.isGuestException());
  }

  @Test
  public void testComp() {
    Value comp = context.eval("nock", "[7 [0 2] [0 3] 0 2]");
    Value r = comp.execute(new Cell(new Cell(1L, 2L), 3L));
    assertEquals(2L, r.getMember("head").as(Number.class));
    assertEquals(1L, r.getMember("tail").as(Number.class));
  }

  @Test
  public void testPush() {
    Value push = context.eval("nock", "[8 [1 42] [0 3] 0 2]");
    Value r = push.execute();
    assertEquals(0L, r.getMember("head").as(Number.class));
    assertEquals(42L, r.getMember("tail").as(Number.class));
  }

  @After
  public void dispose() {
    context.close();
  }
}
