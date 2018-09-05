package net.frodwith.jaque.data;

import java.io.Serializable;

import net.frodwith.jaque.exception.CellRequiredException;

import net.frodwith.jaque.runtime.Mug;
import net.frodwith.jaque.runtime.Equality;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public final class Cell implements Serializable {
  // head and tail are not final because we set them during unifying equals
  public Object head, tail;
  public int mug;
  
  public Cell(Object head, Object tail) {
    this.head = head;
    this.tail = tail;
  }

  public int hashCode() {
    return Mug.get(this);
  }

  public boolean equals(Object o) {
    return (o instanceof Cell) && Equality.equals(this, (Cell) o);
  }

  public static Cell require(Object o) throws CellRequiredException {
    if ( o instanceof Cell ) {
      return (Cell) o;
    }
    else {
      throw new CellRequiredException(o);
    }
  }
}
