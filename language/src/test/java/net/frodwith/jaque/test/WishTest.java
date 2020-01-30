package net.frodwith.jaque.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.PolyglotAccess;

public class WishTest {
  private Context context;
  private Value runtime;


  @Before
  public void initEngine() {
    context = Context.newBuilder()
      .allowPolyglotAccess(PolyglotAccess.ALL)
      .build();
    context.initialize("nock");
    runtime = context.getPolyglotBindings().getMember("nock");
  }

  @Test
  public void testBasicWish() {
    Value formula = context.eval("nock", 
        "[1 12 [1 151 1.836.020.833 116 0] 1 97 98 99 0]").execute();
    Value scry = context.eval("nock", "[1 [1 0 0 42] 0]").execute();
    Value product = runtime.invokeMember("mink", 0L, formula, scry);
    Value head = context.eval("nock", "[0 2]"),
          tail = context.eval("nock", "[0 3]");
    assertEquals(0L, head.execute(product).as(Number.class));
    assertEquals(42L, tail.execute(product).as(Number.class));
    // XX: more tests!
  }
}
