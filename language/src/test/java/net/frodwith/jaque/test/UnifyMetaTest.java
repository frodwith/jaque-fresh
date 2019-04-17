package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.Before;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;

import net.frodwith.jaque.dashboard.Battery;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockClass;
import net.frodwith.jaque.exception.ExitException;

public class UnifyMetaTest {
  Dashboard dashboard;

  @Before
  public void init() {
    this.dashboard = new Dashboard.Builder().build();
  }

  @Test
  public void testFormulas() throws ExitException {
    Source src = Source.newBuilder("nock", "[[1 1] 1 0]", "cell.nock")
      .buildLiteral();
    Context context = Context.create();
    Value make = context.eval(src),
          one = make.execute(),
          two = make.execute();

    assertFalse(one.getMetaObject().getMember("isFormula").as(Boolean.class));
    assertFalse(two.getMetaObject().getMember("isFormula").as(Boolean.class));

    Value product = one.getMetaObject().execute();

    assertTrue(one.getMetaObject().getMember("isFormula").as(Boolean.class));
    assertFalse(two.getMetaObject().getMember("isFormula").as(Boolean.class));

    src = Source.newBuilder("nock", "[5 [0 2] 0 3]", "equal.nock")
      .buildLiteral();
    Value isEqual = context.eval(src);
    assertEquals(0L, isEqual.execute(one, two).as(Number.class));

    assertTrue(one.getMetaObject().getMember("isFormula").as(Boolean.class));
    assertTrue(two.getMetaObject().getMember("isFormula").as(Boolean.class));
  }

  @Test
  public void testCores() throws ExitException {
    Cell a = new Cell(new Cell(1L, 0L), 0L);
    Cell b = new Cell(new Cell(1L, 0L), 0L);

    assertFalse(a.getMeta().hasClass(dashboard));
    assertFalse(b.getMeta().hasClass(dashboard));

    NockClass ca = a.getMeta().getNockClass(a, dashboard);

    assertTrue(a.getMeta().hasClass(dashboard));
    assertFalse(b.getMeta().hasClass(dashboard));

    CellMeta.unify(a.getMeta(), b.getMeta());

    assertTrue(a.getMeta().hasClass(dashboard));
    assertTrue(b.getMeta().hasClass(dashboard));
  }

  @Test
  public void testMugs() throws ExitException {
    Cell a = new Cell(new Cell(1L, 0L), 0L);
    Cell b = new Cell(new Cell(1L, 0L), 0L);

    assertEquals(0, a.getMeta().cachedMug());
    assertEquals(0, b.getMeta().cachedMug());

    int mug = a.getMeta().mug(a);

    assertEquals(mug, a.getMeta().cachedMug());
    assertEquals(0, b.getMeta().cachedMug());

    CellMeta.unify(a.getMeta(), b.getMeta());

    assertEquals(mug, a.getMeta().cachedMug());
    assertEquals(mug, b.getMeta().cachedMug());
  }

  // we don't unify battery meta because of grains
}
