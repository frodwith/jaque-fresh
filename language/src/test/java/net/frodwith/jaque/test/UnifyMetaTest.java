package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
    assertTrue(false);
    /*
    Cell a = new Cell(1L, 0L);
    Cell b = new Cell(1L, 0L);

    assertFalse(a.getMeta().hasFunction(dashboard));
    assertFalse(b.getMeta().hasFunction(dashboard));

    NockFunction fa = a.getMeta().getFunction(a, dashboard);

    assertTrue(a.getMeta().hasFunction(dashboard));
    assertFalse(b.getMeta().hasFunction(dashboard));

    CellMeta.unify(a.getMeta(), b.getMeta());

    assertTrue(a.getMeta().hasFunction(dashboard));
    assertTrue(b.getMeta().hasFunction(dashboard));
    */
  }

  @Test
  public void testCores() throws ExitException {
    assertTrue(false);
    /*
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
    */
  }

  // we don't unify battery meta because of grains
}
