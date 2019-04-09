package net.frodwith.jaque.test;

import java.util.function.BiFunction;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import com.google.common.hash.HashCode;

import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.jet.ChildCore;
import net.frodwith.jaque.jet.JetArm;
import net.frodwith.jaque.jet.AxisArm;
import net.frodwith.jaque.jet.JetHook;
import net.frodwith.jaque.jet.JetTree;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.BatteryHash;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.jet.DecNodeGen;
import net.frodwith.jaque.nodes.jet.AddNodeGen;

import net.frodwith.jaque.test.nodes.*;

/*
!=
=<  fib
=~  %42
~%  %kern  ~  ~
|%
++  version  +
--
~%  %one  +  ~
|%
++  dec
  ~/  %dec
  |=  a=@
  ?:  =(0 a)  !!
  =|  i=@
  |-  ^-  @
  =/  n  +(i)
  ?:  =(n a)
    i
  $(i n)
::
++  add
  ~/  %add
  |=  [a=@ b=@]
  ?:  =(b 0)  a
  %=  $
    a  +(a)
    b  (dec b)
  ==
--
~%  %two  +  ~
|%
++  fib
  ~/  %fib
  |=  n=@
  ^-  @  ~+
  ?:  =(0 n)  0
  ?:  =(1 n)  1
  =/  a  (dec n)
  =/  b  (dec a)
  %+  add
    $(n a)
  $(n b)
--  ==
*/

public class MemoTest {
  private static final String FIB_SOURCE_STRING = "[7 [7 [1 42] 7 [8 [1 0 3] 11 [1.953.718.630 1 1.852.990.827 [1 0] 0] 0 1] 7 [8 [1 [7 [8 [1 0 0] [1 6 [5 [0 13] 1 0] [0 12] 9 2 10 [6 [4 0 12] 8 [9 5 0 7] 9 2 10 [6 0 29] 0 2] 0 1] 0 1] 11 [1.953.718.630 1 6.579.297 [0 7] 0] 0 1] 7 [8 [1 0] [1 6 [5 [1 0] 0 6] [0 0] 8 [1 0] 8 [1 8 [4 0 6] 6 [5 [0 2] 0 62] [0 14] 9 2 10 [6 0 2] 0 3] 9 2 0 1] 0 1] 11 [1.953.718.630 1 6.514.020 [0 7] 0] 0 1] 11 [1.953.718.630 1 6.647.407 [0 3] 0] 0 1] 8 [1 7 [8 [1 0] [1 11 [1.869.440.365 1 0] 6 [5 [1 0] 0 6] [1 0] 6 [5 [1 1] 0 6] [1 1] 8 [8 [9 5 0 15] 9 2 10 [6 0 14] 0 2] 8 [8 [9 5 0 31] 9 2 10 [6 0 6] 0 2] 8 [9 4 0 63] 9 2 10 [6 [7 [0 3] 9 2 10 [6 0 6] 0 7] 7 [0 3] 9 2 10 [6 0 2] 0 7] 0 2] 0 1] 11 [1.953.718.630 1 6.449.510 [0 7] 0] 0 1] 11 [1.953.718.630 1 7.305.076 [0 3] 0] 0 1] 9 2 0 1]";

  private static final Source fibSource =
    Source.newBuilder("nock", FIB_SOURCE_STRING, "fib.nock").buildLiteral();

  private Context context;

  private static ChildCore
    gate(String name, BiFunction<AstContext,Axis,SubjectNode> factory) {
    return new ChildCore(name,
        Axis.CONTEXT,
        new HashCode[0],
        new JetArm[] { new AxisArm(Axis.HEAD, factory) },
        new JetHook[0],
        new ChildCore[0]);
  }

  @BeforeClass
  public static void installJets() {
    NockLanguage.installJetTree("memo",
      new JetTree(new RootCore[] {
        new RootCore("kern", 42L,
          new HashCode[0],
          new JetArm[0],
          new JetHook[0],
          new ChildCore[] {
            new ChildCore("one",
              Axis.TAIL,
              new HashCode[0],
              new JetArm[0],
              new JetHook[0],
              new ChildCore[] {
                gate("dec", (c, ax) ->
                    DecNodeGen.create(new SlotNode(Axis.SAMPLE))),
                gate("add", (c, ax) ->
                    AddNodeGen.create(new SlotNode(Axis.get(12L)),
                                      new SlotNode(Axis.get(13L)))),
                new ChildCore("two",
                  Axis.TAIL,
                  new HashCode[0],
                  new JetArm[0],
                  new JetHook[0],
                  new ChildCore[] {
                    gate("fib", (c, ax) ->
                      new CountNockNode(c, ax, "fib"))})})})}));
  }

  @Before
  public void init() {
    context = Context.newBuilder()
                     .option("nock.jets", "memo")
                     .build();
  }

  @After
  public void dispose() {
    context.close();
  }

  @Test
  public void testFib() {
    Value gate = context.eval(fibSource).execute();
    assertEquals(0, CountNockNode.count("fib"));
    Value product = gate.getMetaObject().invokeMember("2", 10L);
    assertEquals(55L, product.as(Number.class));
    // count is 177 without memoization
    assertEquals(19, CountNockNode.count("fib"));
  }
}
