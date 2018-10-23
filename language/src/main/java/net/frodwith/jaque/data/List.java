package net.frodwith.jaque.data;

import java.util.Iterator;

public class List implements Iterable<Object> {
  private Object noun;
  
  public List(Object noun) {
    this.noun = noun;
  }

  @Override
  public Iterator<Object> iterator() {
    return new Cursor(noun);
  }

  public class Cursor implements Iterator<Object> {
    private Object cur;
    
    public Cursor(Object noun) {
      this.cur = noun;
    }

    @Override
    public boolean hasNext() {
      return cur instanceof Cell;
    }

    @Override
    public Object next() {
      Cell c = (Cell) cur;
      cur = c.tail;
      return c.head;
    }
  }
}
