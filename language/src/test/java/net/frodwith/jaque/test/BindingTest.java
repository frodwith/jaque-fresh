package net.frodwith.jaque.test;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Context;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.test.quickcheck.NounGenerator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class BindingTest {
  Context context;

  @Before
  public void init() {
    context = Context
              .newBuilder("nock")
              .allowAllAccess(true)
              .build();
    context.initialize("nock");
  }

  @After
  public void dispose() {
    context.close();
  }

  // This is just testing the polyglot bindings to jam/cue; the BIG quickcheck
  // on the jam/cue code is in SerialTest.
  @Property(trials=256)
  public void testBindingRoundTrip(@From(NounGenerator.class) Object start) {
    Value nock = context
                 .getPolyglotBindings()
                 .getMember("nock");

    Value jammed = nock.invokeMember("jam", start);
    Value end = nock.invokeMember("cue", jammed);

    // TODO: OK, so we can't use normal equality since equals() in java is
    // reference equality, and that's all Value leaves us. We can't use jaque's
    // Equality.equals() because that doesn't understand interop layer Value
    // objects. Putting toString() on things below is kinda bogus, but I don't
    // know of a way to expose equality otherwise.
    //
    // assertEquals(end, start);
    assertEquals(end.toString(), start.toString());
  }
}
