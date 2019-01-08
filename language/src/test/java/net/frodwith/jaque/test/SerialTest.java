package net.frodwith.jaque.test;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.HoonSerial;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.test.quickcheck.NounGenerator;

import static org.junit.Assert.assertTrue;
import static net.frodwith.jaque.parser.CustomParser.simple;

@RunWith(JUnitQuickcheck.class)
public class SerialTest {
  private static void aeq(String msg, Object a, Object b) {
    assertTrue(msg, Equality.equals(a, b));
  }

  private static Object c(String s) {
    return Cords.fromString(s);
  }

  @Test
  public void testExamples() throws ExitException {
    Object a = 5456L, b = 42L;
    aeq("jam 1", a, HoonSerial.jam(b));
    aeq("cue 1", b, HoonSerial.cue(a));

    a = simple("1.054.973.063.816.666.730.241");
    b = new Cell(c("foo"), c("bar"));
    aeq("jam 2", a, HoonSerial.jam(b));
    aeq("cue 2", b, HoonSerial.cue(a));

    // this failed once
    Object at = simple("16.167.128.708.910.327.756");
    aeq("64-bit direct atom", at, HoonSerial.cue(HoonSerial.jam(at)));
  }

  @Property(trials=8192)
  public void testRoundTrip(@From(NounGenerator.class) Object noun)
    throws ExitException {
    aeq("quickcheck", noun, HoonSerial.cue(HoonSerial.jam(noun)));
  }
}
