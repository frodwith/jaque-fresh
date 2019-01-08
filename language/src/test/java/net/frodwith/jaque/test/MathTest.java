package net.frodwith.jaque.test;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.test.quickcheck.AtomGenerator;

import static net.frodwith.jaque.parser.CustomParser.simple;
import static net.frodwith.jaque.test.Util.assertNounEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(JUnitQuickcheck.class)
public class MathTest {
  @Test
  public void addExamples() throws ExitException {
    assertNounEquals("ltuae", 42L, HoonMath.add(40L, 2L));
    System.err.println(HoonMath.add(5L, 0xFFFFFFFFFFFFFFFFL));
    assertNounEquals("add to max long", simple("18.446.744.073.709.551.620"),
      HoonMath.add(5L, 0xFFFFFFFFFFFFFFFFL));
  }

  @Test
  public void subExamples() throws ExitException {
    assertNounEquals("ltuae", 40L, HoonMath.sub(42L, 2L));
    assertNounEquals("sub to max long", 0xFFFFFFFFFFFFFFFFL,
      HoonMath.sub(simple("18.446.744.073.709.551.620"), 5L));
  }

  @Test
  public void mulExamples() throws ExitException {
    assertNounEquals("ltuae", 42L, HoonMath.mul(6L, 7L));
    assertNounEquals("overmul", simple("510.015.580.149.921.683.079.168"),
      HoonMath.mul(805306368L, 633318697598976L));
  }

  @Test
  public void divExamples() throws ExitException {
    assertNounEquals("ltuae", 7L, HoonMath.div(42L, 6L));
    assertNounEquals("underdiv", 633318697598976L,
      HoonMath.div(simple("510.015.580.149.921.683.079.168"), 805306368L));
  }

  @Test
  public void modExamples() throws ExitException {
    assertNounEquals("smol", 2L, HoonMath.mod(44L, 6L));
    assertNounEquals("undermod", 1000L,
      HoonMath.mod(simple("510.015.580.149.921.683.080.168"), 805306368L));
  }

  // subtraction reverses addition
  @Property
  public void addSub(@From(AtomGenerator.class) Object a,
                     @From(AtomGenerator.class) Object b)
    throws ExitException {
    Object c = HoonMath.add(a, b);
    assertNounEquals(a, HoonMath.sub(c, b));
    assertNounEquals(b, HoonMath.sub(c, a));
  }

  // division reverses multiplication
  @Property
  public void mulDiv(@From(AtomGenerator.class) Object a,
                     @From(AtomGenerator.class) Object b)
    throws ExitException {
    assumeTrue(!Atom.isZero(a) && !Atom.isZero(b));
    Object c = HoonMath.mul(a, b);
    assertNounEquals(a, HoonMath.div(c, b));
    assertNounEquals(b, HoonMath.div(c, a));
  }

  // (5x + 1) % 5 == 1
  @Property
  public void addMod(@From(AtomGenerator.class) Object a) throws ExitException {
    assumeTrue(!Atom.isZero(a));
    assertNounEquals(1L, HoonMath.mod(
          HoonMath.add(1L, HoonMath.mul(a, 5L)),
          5L));
  }
}
