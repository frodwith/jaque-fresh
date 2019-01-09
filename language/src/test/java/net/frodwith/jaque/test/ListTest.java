package net.frodwith.jaque.test;

import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

import net.frodwith.jaque.NounFunction;
import net.frodwith.jaque.NounPredicate;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static net.frodwith.jaque.test.Util.assertNounEquals;

public class ListTest {
  @Test
  public void testMake() throws ExitException {
    assertNounEquals(0L, Lists.make());
    assertNounEquals(new Cell(1L, 0L), Lists.make(1L));
    assertNounEquals(new Qual(1L, 2L, 3L, 0L).toNoun(),
                     Lists.make(1L, 2L, 3L));
  }

  @Test
  public void testIterator() throws ExitException {
    Lists.Iterator i = new Lists.Iterator(Lists.make(1L, 2L, 3L));
    assertTrue(i.hasNext());
    assertEquals(1L, i.next());
    assertTrue(i.hasNext());
    assertEquals(2L, i.next());
    assertTrue(i.hasNext());
    assertEquals(3L, i.next());
    assertFalse(i.hasNext());
  }

  @Test
  public void testDeque() throws ExitException {
    Deque<Object> d = Lists.toDeque(Lists.make(0L, 1L, 2L));
    assertFalse(d.isEmpty());
    assertEquals(2L, d.pop());
    assertFalse(d.isEmpty());
    assertEquals(1L, d.pop());
    assertFalse(d.isEmpty());
    assertEquals(0L, d.pop());
    assertTrue(d.isEmpty());
  }

  @Test
  public void testReel() throws ExitException {
    List<Object> items = new ArrayList<>();
    NounFunction add = (sample) -> {
      Cell args = Cell.require(sample);
      items.add(args.head);
      return HoonMath.add(args.head, args.tail);
    };
    assertEquals(10L, Lists.reel(add, 0L, Lists.make(0L, 1L, 2L, 3L, 4L)));
    assertArrayEquals(items.toArray(new Object[items.size()]),
                      new Object[] { 4L, 3L, 2L, 1L, 0L });
  }

  @Test
  public void testRoll() throws ExitException {
    List<Object> items = new ArrayList<>();
    NounFunction mul = (sample) -> {
      Cell args = Cell.require(sample);
      items.add(args.head);
      return HoonMath.mul(args.head, args.tail);
    };
    assertEquals(24L, Lists.roll(mul, 1L, Lists.make(1L, 2L, 3L, 4L)));
    assertArrayEquals(items.toArray(new Object[items.size()]),
                      new Object[] { 1L, 2L, 3L, 4L });
  }

  @Test
  public void testTurn() throws ExitException {
    NounFunction dub = (sample) -> HoonMath.mul(2L, sample);
    assertNounEquals(Lists.make(2L, 4L, 6L),
                     Lists.turn(dub, Lists.make(1L, 2L, 3L)));
  }

  @Test
  public void testWeld() throws ExitException {
    assertNounEquals(Lists.make(1L, 2L, 3L, 4L, 5L, 6L),
                     Lists.weld(Lists.make(1L, 2L, 3L),
                                Lists.make(4L, 5L, 6L)));
  }

  @Test
  public void testLien() throws ExitException {
    NounPredicate even = (sample) -> Atom.isZero(HoonMath.mod(sample, 2L));
    assertTrue(Lists.lien(even, Lists.make(1L, 2L, 3L)));
    assertFalse(Lists.lien(even, Lists.make(1L, 3L, 5L)));
  }

  @Test
  public void testLevy() throws ExitException {
    NounPredicate odd = (sample) -> !Atom.isZero(HoonMath.mod(sample, 2L));
    assertFalse(Lists.levy(odd, Lists.make(1L, 2L, 3L)));
    assertTrue(Lists.levy(odd, Lists.make(1L, 3L, 5L)));
  }

  @Test
  public void testLent() throws ExitException {
    assertEquals(3L, Lists.lent(Lists.make(1L, 2L, 3L)));
    assertEquals(5L, Lists.lent(Lists.make(0L, 1L, 2L, 3L, 4L)));
  }

  @Test
  public void testSlag() throws ExitException {
    assertNounEquals(Lists.make(2L, 3L),
                     Lists.slag(2L, Lists.make(0L, 1L, 2L, 3L)));
  }

  @Test
  public void testFlop() throws ExitException {
    assertNounEquals(Lists.make(1L, 2L, 3L),
                     Lists.flop(Lists.make(3L, 2L, 1L)));
  }

  @Test
  public void testLoss() throws ExitException {
    assertNounEquals(Tapes.fromString("oobaz"),
                     Lists.loss(Tapes.fromString("foobarbaz"),
                                Tapes.fromString("goobadguz")));
  }
}
