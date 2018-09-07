package net.frodwith.jaque.parser;

import java.util.Map;

public final class ParsedNoun {
  private final Object noun;
  private final SourceSection sourceSection;
  private final Map<Object,IndexLength> where;

  public ParsedNoun(Object noun, SourceSection rootSection, Map<Object,SourceSection> where) {
    this.noun  = noun;
    this.where = where;
  }

  public static ParsedNoun fromCell(Cell sourceCell) {
    Source source = makeSource(sourceCell);
  }

  private static Source makeSource(Cell sourceCell) {
    String text = SimpleDotted.format(sourceCell);

    return Source.newBuilder("nock", text, "(generated)")
      .internal(true)
      .buildLiteral();
  }

  private final static class CellFrame implements PopulateFrame {
    public void populate(Map<Object,SourceSection> where, ArrayDeque<PopulateFrame> stack) {

    }
  }

  public IndexLength axisSection(Object axis) {
    return where.get(axis);
  }
}
