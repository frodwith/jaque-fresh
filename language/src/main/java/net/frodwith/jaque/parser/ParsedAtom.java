package net.frodwith.jaque.parser;

public final class ParsedAtom extends ParsedNoun {
  public final Object atom;

  public ParsedAtom(Object atom, int position, int length) {
    super(position, length);
    this.atom = atom;
  }

  public Object toNoun() {
    return atom;
  }
}
