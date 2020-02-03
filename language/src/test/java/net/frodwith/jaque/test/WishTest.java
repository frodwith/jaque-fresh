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
  private Value runtime, wisher, head, tail, same, bc, abc, abcd, cd, test;

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
    bc   = context.eval("nock", "[1 98 99 0]").execute();
    abc  = context.eval("nock", "[1 97 98 99 0]").execute();
    abcd = context.eval("nock", "[1 97 98 99 100]").execute();
    cd   = context.eval("nock", "[1 99 100]").execute();
// [~ ~] on empty path,
// block on paths starting with /b,
// !! crash on /c,
// else ``42
    test = context.eval("nock",
      "[1 [6 [6 [3 0 13] [1 1] 1 0] [1 0 0] 6 [5 [0 26] 1 98] [1 0] 6 [5 [0 26] 1 99] [0 0] 1 0 0 42] 0 0]"
    ).execute();
  }

  private Object scry(Object path, Object... gates) {
    Object[] arguments = new Object[gates.length+2];
    arguments[0] = path;
    arguments[1] = wisher;
    for ( int i = 0; i < gates.length; ++i ) {
      arguments[i+2] = gates[i];
    }
    return runtime.invokeMember("mink", arguments);
  }

  private void assertSuccess(Object got, Object expected) {
    assertEquals(0L, head.execute(got).as(Number.class));
    assertEquals(0L, same.execute(tail.execute(got), expected).as(Number.class));
  }

  private void assertBlock(Object got, Object path) {
    assertEquals(1L, head.execute(got).as(Number.class));
    assertEquals(0L, same.execute(tail.execute(got), path).as(Number.class));
  }

  private void assertNever(Object got) {
    assertEquals(2L, head.execute(got).as(Number.class));
  }

  @FunctionalInterface
  private interface Executable {
    public abstract void execute();
  }

  private void assertThrows(Executable code) {
    try {
      code.execute();
      throw new AssertionError("code did not throw");
    }
    catch ( PolyglotException e ) {
    }
  }

  @Test
  public void testBasic() {
    Value always42 = context.eval("nock", "[1 [1 0 0 42] 0]").execute();
    assertSuccess(scry(0L, always42), 42L);
  }

  @Test
  public void testCrash() {
    Value crash = context.eval("nock", "[1 [0 0] 0 0]").execute();
    assertThrows(() -> scry(0L, crash));
  }

  @Test
  public void testFull() {
    assertNever(scry(0L, test));
    assertThrows(() -> scry(cd, test));
    assertBlock(scry(bc, test), bc);
    assertSuccess(scry(abc, test), 42L);
  }

  @Test
  public void testNested() {
    Value passThru = context.eval("nock", "[1 [[1 0] [1 0] 12 [0 12] 0 13] 0 0]")
      .execute();;
    assertSuccess(scry(abcd, test, passThru, passThru), 42L);
    assertBlock(scry(bc, test, passThru), bc);
    assertThrows(() -> scry(0L, test, passThru));
    assertThrows(() -> scry(cd, test));
  }
}
