package net.frodwith.jaque.data;

import com.oracle.truffle.api.source.SourceSection;
import java.util.Map;

public final class SourceMappedNoun {
  public static final class IndexLength {
    public final int index, length;

    public IndexLength(int index, int length) {
      this.index = index;
      this.length = length;
    }
  }

  public final SourceSection sourceSection;
  public final Object noun;
  private final Map<Object,IndexLength> locations;

  public SourceMappedNoun(SourceSection sourceSection,
                          Map<Object,IndexLength> locations,
                          Object noun) {
    this.sourceSection = sourceSection;
    this.locations = locations;
    this.noun = noun;
  }

  public SourceSection lookupAxis(Object axis) {
    IndexLength il = locations.get(axis);
    if ( null == il ) {
      return null;
    }
    return sourceSection.getSource().createSection(il.index, il.length);
  }

  public Object getNoun() {
    return noun;
  }

  public static SourceMappedNoun fromCell(Cell cell) {
    StringWriter out = new StringWriter();
    Map<Object,IndexLength> axisMap = MappedNounPrinter.print(out, cell);
    String text = out.toString();
    Source source = Source.newBuilder("nock", text, "(generated)")
      .internal(true)
      .build();
    SourceSection whole = source.createSection(0, text.length());
    return new SourceMappedNoun(whole, axisMap, cell);
  }
}
