package net.frodwith.jaque.data;

import net.frodwith.jaque.exception.CellRequiredException;

public final class Trel {
  public final Object p, q, r;

  public Trel(Object p, Object q, Object r) {
    this.p = p;
    this.q = q;
    this.r = r;
  }

  public static Trel require(Object o) throws CellRequiredException {
    Cell trel = Cell.require(o),
         tail = Cell.require(trel.tail);
    return new Trel(trel.head, tail.head, tail.tail);
  }

  public Cell toNoun() {
    return new Cell(p, new Cell(q, r));
  }
}
