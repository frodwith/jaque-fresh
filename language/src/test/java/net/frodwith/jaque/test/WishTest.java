package net.frodwith.jaque.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.PolyglotException;

public class WishTest {
  private Context context;
  private Value runtime, wisher, head, tail, same;

  // scry function: $-(path (unit (unit *)))
  // if ~, block (answer later)
  // if [~ ~], crash (never answer)
  // if [~ ~ answer], supply answer

  @Before
  public void initEngine() {
    context = Context.newBuilder()
      .allowPolyglotAccess(PolyglotAccess.ALL)
      .build();
    context.initialize("nock");
    runtime = context.getPolyglotBindings().getMember("nock");
    // wisher uses the whole subject as the path for a hoon-style scry
    wisher = context.eval("nock", 
        "[1 12 [1 151 1.836.020.833 116 0] 0 1]").execute();
    head = context.eval("nock", "[0 2]");
    tail = context.eval("nock", "[0 3]");
    same = context.eval("nock", "[5 [0 2] 0 3]");
  }

  private Object scry(Object path, Object scry) {
    return runtime.invokeMember("mink", path, wisher, scry);
  }

  private void assertSuccess(Object got, Object expected) {
    assertEquals(0L, head.execute(got).as(Number.class));
    assertEquals(0L, same.execute(tail.execute(got), expected).as(Number.class));
  }

  private void assertBlock(Object got, Object path) {
    assertEquals(1L, head.execute(got).as(Number.class));
    assertEquals(0L, same.execute(tail.execute(got), path).as(Number.class));
  }

  private void assertCrash(Object got) {
    assertEquals(2L, head.execute(got).as(Number.class));
  }

  @Test
  public void testBasicWish() {
    Value always42 = context.eval("nock", "[1 [1 0 0 42] 0]").execute();
    assertSuccess(scry(0L, always42), 42L);
  }

  @Test
  public void testBlock() {
    // [~ ~] on empty path, block on paths starting with /b, 42 for all else
    Value gate = context.eval("nock",
      "[1 [6 [6 [3 0 13] [1 1] 1 0] [1 0 0] 6 [5 [0 26] 1 98] [1 0] 1 0 0 42] [0 0] 0]"
    ).execute(),
          bc = context.eval("nock", "[1 98 99 0]").execute(),
          abc = context.eval("nock","[1 97 98 99 0]").execute();

    assertCrash(scry(0L, gate));
    assertBlock(scry(bc, gate), bc);
    assertSuccess(scry(abc, gate), 42L);
  }

  @Test
  public void testCrash() {
    Value crash = context.eval("nock", "[1 [0 0] 0 0]").execute();
    boolean threw = false;
    try {
      scry(0L, crash);
    }
    catch ( PolyglotException e ) {
      threw = true;
    }
    assertTrue(threw);
  }

  @Test
  public void testNested() {
    // mink should take a fly and an executable, then i can do two layers
    // should change name from mink to virtualize
  }
}
