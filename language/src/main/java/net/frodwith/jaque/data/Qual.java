package net.frodwith.jaque.data;

import net.frodwith.jaque.exception.ExitException;

public final class Qual {
  public final Object p, q, r, s;

  public Qual(Object p, Object q, Object r, Object s) {
    this.p = p;
    this.q = q;
    this.r = r;
    this.s = s;
  }

  public static Qual require(Object o) throws ExitException {
    Cell first = Cell.require(o);
    Trel rest = Trel.require(first.tail);
    return new Qual(first.head, rest.p, rest.q, rest.r);
  }

  public Cell toNoun() {
    return new Cell(p, new Cell(q, new Cell(r, s)));
  }
}
