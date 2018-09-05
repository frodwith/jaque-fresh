package net.frodwith.jaque.parser;

public abstract class ParsedNoun {
  public final int position;
  public final int length;

  protected ParsedNoun(int position, int length) {
    this.position = position;
    this.length = length;
  }

  public abstract Object toNoun();
}
