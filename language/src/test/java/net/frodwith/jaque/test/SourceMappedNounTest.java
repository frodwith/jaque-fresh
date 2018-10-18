package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.runtime.Equality;

import net.frodwith.jaque.parser.CustomParser;
import net.frodwith.jaque.exception.ExitException;

import java.util.logging.*;

public final class SourceMappedNounTest {
  private static final Logger LOGGER = Logger.getLogger( SourceMappedNounTest.class.getName() );

  private SourceMappedNoun parse(String name, String str) throws ExitException {
    Source src = Source.newBuilder(NockLanguage.ID, str, name).build();
    return CustomParser.parse(src.createSection(0, str.length()));
  }

  @Test
  public void testSmall() throws ExitException {
    SourceMappedNoun r = parse("small", "  42");
    assertEquals(42L, r.noun);
    assertEquals("42", r.lookupAxis(Axis.IDENTITY).getCharacters());
  }

  @Test
  public void testDotted() throws ExitException {
    SourceMappedNoun r = parse("dotted", "  1.042  ");
    assertEquals(1042L, r.noun);
    assertEquals("1.042", r.lookupAxis(Axis.IDENTITY).getCharacters());
  }

  @Test
  public void testBig() throws ExitException {
    Object a = new BigAtom(new int[] { 0xdeadbeef, 0xbeefdead, 0xfeedbeef });
    assertEquals(a, parse("big", "78.896.609.586.032.353.644.659.982.063").noun);
  }
  
  @Test
  public void testWeirdString() throws ExitException {
    SourceMappedNoun r = parse("cell", "  [ 40 [0   42]   1.042 ] ");
    assertEquals("deep", r.noun,
        new Cell(40L, new Cell(new Cell(0L, 42L), 1042L)));
    assertEquals("-", "40", r.lookupAxis(Axis.HEAD).getCharacters());
    assertEquals("+", "[0   42]   1.042 ]", r.lookupAxis(Axis.TAIL).getCharacters());
    assertEquals("+<", "[0   42]", r.lookupAxis(Axis.SAMPLE).getCharacters());
    assertEquals("+<-", "0", r.lookupAxis(new Axis(12L)).getCharacters());
    assertEquals("+<+", "42", r.lookupAxis(new Axis(13L)).getCharacters());
    assertEquals("+>", "1.042", r.lookupAxis(Axis.CONTEXT).getCharacters());
    assertEquals(".", "[ 40 [0   42]   1.042 ]",
      r.lookupAxis(Axis.IDENTITY).getCharacters());
  }

  @Test
  public void testFromCell() throws ExitException {
    SourceMappedNoun r = SourceMappedNoun.fromCell(
      new Cell(40L, new Cell(new Cell(0L, 42L), 1042L)));
    assertEquals("-", "40", r.lookupAxis(Axis.HEAD).getCharacters());
    assertEquals("+", "[0 42] 1.042]", r.lookupAxis(Axis.TAIL).getCharacters());
    assertEquals("+<", "[0 42]", r.lookupAxis(Axis.SAMPLE).getCharacters());
    assertEquals("+<-", "0", r.lookupAxis(new Axis(12L)).getCharacters());
    assertEquals("+<+", "42", r.lookupAxis(new Axis(13L)).getCharacters());
    assertEquals("+>", "1.042", r.lookupAxis(Axis.CONTEXT).getCharacters());
    assertEquals(".", "[40 [0 42] 1.042]",
      r.lookupAxis(Axis.IDENTITY).getCharacters());
  }
}
