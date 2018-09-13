package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.runtime.Equality;

import net.frodwith.jaque.parser.CustomParser;
import net.frodwith.jaque.exception.Fail;

import java.util.logging.*;

public final class SourceMappedNounTest {
  private static final Logger LOGGER = Logger.getLogger( SourceMappedNounTest.class.getName() );

  private SourceMappedNoun parse(String name, String str) throws Fail {
    Source src = Source.newBuilder(NockLanguage.ID, str, name).build();
    return CustomParser.parse(src.createSection(0, str.length()));
  }

  @Test
  public void testSmall() throws Fail {
    SourceMappedNoun r = parse("small", "  42");
    assertEquals(42L, r.noun);
    assertEquals("42", r.lookupAxis(1L).getCharacters());
  }

  @Test
  public void testDotted() throws Fail {
    SourceMappedNoun r = parse("dotted", "  1.042  ");
    assertEquals(1042L, r.noun);
    assertEquals("1.042", r.lookupAxis(1L).getCharacters());
  }
  
  @Test
  public void testCell() throws Fail {
    SourceMappedNoun r = parse("cell", "  [ 40 [0   42]   1.042 ] ");
    assertEquals("deep", r.noun,
        new Cell(40L, new Cell(new Cell(0L, 42L), 1042L)));
    assertEquals("-", "40", r.lookupAxis(2L).getCharacters());
    assertEquals("+", "[0   42]   1.042 ]", r.lookupAxis(3L).getCharacters());
    assertEquals("+<", "[0   42]", r.lookupAxis(6L).getCharacters());
    assertEquals("+<-", "0", r.lookupAxis(12L).getCharacters());
    assertEquals("+<+", "42", r.lookupAxis(13L).getCharacters());
    assertEquals("+>", "1.042", r.lookupAxis(7L).getCharacters());
    assertEquals(".", "[ 40 [0   42]   1.042 ]",
      r.lookupAxis(1L).getCharacters());
  }
}

/*
public class SimpleParserTest {
  @Test
  public void testSmall() {
    assertEquals(42L, parse("42").noun);
  }
  
  @Test
  public void testBig() {
    Object a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(a, parse("78.896.609.586.032.353.644.659.982.063").toNoun());
  }

  @Test
  public void testCell() {
    Cell a = new Cell(0L, new Cell(1L, 2L));
    assertEquals(a, parse("[0 1 2]").toNoun());
  }
}
*/
