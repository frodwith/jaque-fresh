package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.PolyglotException;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.parser.SimpleAtomParser;

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
    assertEquals(0L, product.getArrayElement(0).as(Number.class));
    assertEquals(42L, product.getArrayElement(1).as(Number.class));
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
    assertEquals(0L, product.getArrayElement(0).as(Number.class));
    assertEquals(42L, product.getArrayElement(1).as(Number.class));
  }

  @Test
  public void testFragment() {
    Cell data = new Cell(1L, new Cell(2L, 3L));

    assertEquals("-", 1L, context.eval("nock", "[0 2]").execute(data).as(Number.class));
    assertEquals("+<", 2L, context.eval("nock", "[0 6]").execute(data).as(Number.class));
    assertEquals("+>", 3L, context.eval("nock", "[0 7]").execute(data).as(Number.class));

    Value tail = context.eval("nock", "[0 3]").execute(data);
    assertEquals("+memhead", 2L, tail.getArrayElement(0).as(Number.class));
    assertEquals("+memtail", 3L, tail.getArrayElement(1).as(Number.class));
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

    Object big = SimpleAtomParser.parse("1000000000000000000000000");
    BigAtom bigger = (BigAtom) SimpleAtomParser.parse("1000000000000000000000001");
    int[] got = bump.execute(big).getMember("words").as(int[].class);
    assertArrayEquals("huge", bigger.words, got);

    got = bump.execute(0xFFFFFFFFFFFFFFFFL).getMember("words").as(int[].class);
    assertArrayEquals("max", new int[] {0,0,1}, got);
  }

  @Test
  public void testSame() {
    Value same = context.eval("nock", "[5 [0 2] 0 3]");
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
  public void testFunkyIf() {
    Value test = context.eval("nock", "[6 [0 1] 1 1 42]");
    PolyglotException thrown = null;

    try {
      assertEquals(42L, test.execute(1L).as(Number.class));
    }
    catch ( PolyglotException e ) {
      thrown = e;
    }
    assertNull(thrown);

    try {
      test.execute(0L);
    }
    catch ( PolyglotException e ) {
      thrown = e;
    }
    assertNotNull(thrown);
    assertTrue(thrown.isGuestException());
  }

  @Test
  public void testComp() {
    Value comp = context.eval("nock", "[7 [0 2] [0 3] 0 2]");
    Value r = comp.execute(new Cell(new Cell(1L, 2L), 3L));
    assertEquals(2L, r.getArrayElement(0).as(Number.class));
    assertEquals(1L, r.getArrayElement(1).as(Number.class));
  }

  @Test
  public void testPush() {
    Value push = context.eval("nock", "[8 [1 42] [0 3] 0 2]");
    Value r = push.execute();
    assertEquals(0L, r.getArrayElement(0).as(Number.class));
    assertEquals(42L, r.getArrayElement(1).as(Number.class));
  }

  @Test
  public void testPull() {
    Value pull = context.eval("nock", "[9 2 1 [0 6] 42 0]");
    assertEquals(42L, pull.execute().as(Number.class));
  }

  @Test
  public void testEdit() {
    Value edit = context.eval("nock", "[7 [10 [6 0 7] 0 1] 0 6]");
    Cell subject = new Cell(0L, new Cell(0L, 42L));
    assertEquals(42L, edit.execute(subject).as(Number.class));
    
    Value nuke = context.eval("nock", "[10 [1 1 42] 0 2]");
    assertEquals(42L, nuke.execute(new Cell(0L, 0L)).as(Number.class));

    PolyglotException bails = null;
    try {
      nuke.execute(0L);
    }
    catch (PolyglotException e) {
      bails = e;
    }
    assertNotNull(bails);
    assertTrue(bails.isGuestException());
  }

  @Test
  public void testValidSpotHint() {
    Value spot = context.eval("nock", "[11 [1.953.460.339 1 [1.685.027.454 1.701.670.760 164.266.780.346.346.260.696.372.364.564.576.299.602.085.886.061.464.870.561.264.254 0] [1 73] 1 74] 1 33]");
    Cell subject = new Cell(2L, 3L);
    assertEquals(33L, spot.execute(subject).as(Number.class));
  }

  @Test
  public void testInvalidHintNumberValidCode() {
    Value spot = context.eval("nock", "[11 [339 [1 0]] 1 33]");
    Cell subject = new Cell(2L, 3L);
    assertEquals(33L, spot.execute(subject).as(Number.class));
  }

  // TODO: Figure out how to build a straight wrong value at compile time so we
  // can test runtime exceptions later.
  /*
  @Test
  public void testInvalidValidSpotHintInvalidCode() {
    // We can't even parse a noun which is invalid like this.
    Value spot = context.eval("nock", "[11 [339 [89 0]] 1 33]");
    Cell subject = new Cell(2L, 3L);

    // In the above, we should fail with a bad nock opcode 87
    RuntimeException bails = null;
    try {
      spot.execute(subject);
    }
    catch (RuntimeException e) {
      bails = e;
    }
    assertNotNull(bails);
  }
  */

  @After
  public void dispose() {
    context.close();
  }
}
