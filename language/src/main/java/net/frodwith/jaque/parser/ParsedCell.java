package net.frodwith.jaque.parser;

public final class ParsedCell extends ParsedNoun {
  public final ParsedNoun head;
  public final ParsedNoun tail;

  public ParsedCell(ParsedNoun head, ParsedNoun tail, int position, int length) {
    super(position, length);
    this.head = head;
    this.tail = tail;
  }

  public Object toNoun() {
    return new Cell(head.toNoun(), tail.toNoun());
  }
}
