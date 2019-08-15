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

  @Test
  public void testExamples() throws ExitException {
    Object a = 5456L, b = 42L;
    aeq("jam 1", a, HoonSerial.jam(b));
    aeq("cue 1", b, HoonSerial.cue(a));

    a = simple("1.054.973.063.816.666.730.241");
    b = new Cell(Cords.fromString("foo"), Cords.fromString("bar"));
    aeq("jam 2", a, HoonSerial.jam(b));
    aeq("cue 2", b, HoonSerial.cue(a));

    // this failed once
    Object at = simple("16.167.128.708.910.327.756");
    aeq("64-bit direct atom", at, HoonSerial.cue(HoonSerial.jam(at)));
  }

  @Test
  public void testMoreExamples() throws ExitException {
    aeq("jam 1", 0xcL, HoonSerial.jam(1L));
    aeq("cue 1", 1L, HoonSerial.cue(HoonSerial.jam(1L)));

    {
      Cell a = new Cell(1L, 2L);
      aeq("jam 2", 0x1231L, HoonSerial.jam(a));

      Cell b = Cell.require(HoonSerial.cue(HoonSerial.jam(a)));
      aeq("cue 2 head", 1L, b.head);
      aeq("cue 2 tail", 2L, b.tail);
    }

    {
      Cell a = new Cell(1L, new Cell(2L, 3L));
      aeq("jam 3", 0x344871L, HoonSerial.jam(a));

      Cell b = Cell.require(HoonSerial.cue(HoonSerial.jam(a)));
      aeq("cue 3 head", 1L, b.head);
      Cell c = Cell.require(b.tail);
      aeq("cue 3 tail head", 2L, c.head);
      aeq("cue 3 tail tail", 3L, c.tail);
    }

    {
      Cell a = new Cell(new Cell(1L, 2L), 3L);
      aeq("jam 4", 0x3448c5L, HoonSerial.jam(a));

      Cell b = Cell.require(HoonSerial.cue(HoonSerial.jam(a)));
      Cell c = Cell.require(b.head);
      aeq("cue 4 head head", 1L, c.head);
      aeq("cue 4 head tail", 2L, c.tail);
      aeq("cue 4 tail", 3L, b.tail);
    }

    {
      Cell z = new Cell(1L, 2L);
      Cell a = new Cell(z, new Cell(z, z));
      aeq("jam/cue 5", a, HoonSerial.cue(HoonSerial.jam(a)));
    }

    {
      Object z = Cords.fromString("abcdefjhijklmnopqrstuvwxyz");
      Cell a = new Cell(z, new Cell(2L, new Cell(3L, z)));
      aeq("jam/cue 6", a, HoonSerial.cue(HoonSerial.jam(a)));
    }

    {
      //  observed failure value from u3 implementation
      //
      Object a =
        simple("[[[1 [[2 [[3 [[4 [[5 6 [7 [[8 0] 0]]] 0]] 0]] 0]] 0]] 0] 0]");
      aeq("jam/cue 7", a, HoonSerial.cue(HoonSerial.jam(a)));
    }
  }

  @Property(trials=8192)
  public void testRoundTrip(@From(NounGenerator.class) Object noun)
    throws ExitException {
    aeq("quickcheck", noun, HoonSerial.cue(HoonSerial.jam(noun)));
  }
}
