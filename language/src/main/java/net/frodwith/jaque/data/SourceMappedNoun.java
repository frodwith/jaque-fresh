package net.frodwith.jaque.data;

import java.io.StringWriter;
import java.io.IOException;
import java.util.Map;

import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.source.Source;

import net.frodwith.jaque.printer.MappedNounPrinter;

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
    Map<Object,IndexLength> axisMap;
    try {
      axisMap = MappedNounPrinter.print(out, cell);
    }
    catch (IOException e) {
      // StringWriter doesn't throw IOException
      return null;
    }
    String text = out.toString();
    Source source = Source.newBuilder(text)
      .language("nock")
      .name("(generated)")
      .internal()
      .build();
    SourceSection whole = source.createSection(0, text.length());
    return new SourceMappedNoun(whole, axisMap, cell);
  }
}
