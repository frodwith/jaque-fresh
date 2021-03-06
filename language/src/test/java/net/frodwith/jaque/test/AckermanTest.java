package net.frodwith.jaque.test;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;

import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.jet.ChildCore;
import net.frodwith.jaque.jet.JetArm;
import net.frodwith.jaque.jet.AxisArm;
import net.frodwith.jaque.jet.JetHook;
import net.frodwith.jaque.jet.JetTree;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.dashboard.BatteryHash;

import net.frodwith.jaque.test.nodes.*;

/*
::  ["cc674e8bd17242c2978f95620a9aea7fa8f663b3857167f60ed620e5358c68ce"
::   "e62f14d11dd53a53887273494f6c12f653470adf9392ac35ab69781882abd38b"]
::  =-  =<  [(mash -:dec) (mash +>-:dec)]
::      |%
::      ++  hexi
::        |=  a=@
::        ^-  tape
::        =/  t  (slag 2 (scow %ux a))
::        ?:  =(2 (lent t))
::          t
::        '0'^t
::      ++  mash
::        |=  a=*
::        =/  h  (shax (jam a))
::        =|  i=@
::        |-  ^-  tape
::        ?.  (lth i 32)  ""
::        %+  weld  (hexi (cut 3 [i 1] h))
::        $(i +(i))
::      --
=<  ack
=>  %kack
~%  %kack  ~  ~
|%
++  dec
  ~/  %dec
  |=  a=@
  =|  i=@
  |-
  =+  n=+(i)
  ?:  =(n a)
    i
  $(i n)
++  ack
  ~/  %ack
  |=  [m=@ n=@]
  ?~  m  +(n)
  ?~  n  $(m (dec m), n 1)
  $(m (dec m), n $(n (dec n)))
--
*/

public class AckermanTest {
  private static final String ACK_SOURCE_STRING = "[7 [7 [1 1.801.675.115] 8 [1 [7 [8 [1 0] [1 8 [1 0] 8 [1 8 [4 0 6] 6 [5 [0 2] 0 62] [0 14] 9 2 10 [6 0 2] 0 3] 9 2 0 1] 0 1] 11 [1.953.718.630 1 6.514.020 [0 7] 0] 0 1] 7 [8 [1 0 0] [1 6 [5 [1 0] 0 12] [4 0 13] 6 [5 [1 0] 0 13] [9 2 10 [6 [8 [9 4 0 7] 9 2 10 [6 0 28] 0 2] 1 1] 0 1] 9 2 10 [6 [8 [9 4 0 7] 9 2 10 [6 0 28] 0 2] 9 2 10 [13 8 [9 4 0 7] 9 2 10 [6 0 29] 0 2] 0 1] 0 1] 0 1] 11 [1.953.718.630 1 7.037.793 [0 7] 0] 0 1] 11 [1.953.718.630 1 1.801.675.115 [1 0] 0] 0 1] 9 5 0 1]";
  private static final Source ackSource = 
    Source.newBuilder("nock", ACK_SOURCE_STRING, "ackerman.nock").buildLiteral();

  private static final String DEC_HASH =
    "cc674e8bd17242c2978f95620a9aea7fa8f663b3857167f60ed620e5358c68ce";

  private static final String KACK_HASH =
    "e62f14d11dd53a53887273494f6c12f653470adf9392ac35ab69781882abd38b";

  @BeforeClass
  public static void installJets() {
    NockLanguage.installJetTree("ack", 
      new JetTree(new RootCore[] {
        new RootCore("kack",
          Cords.fromString("kack"),
          new BatteryHash[] { BatteryHash.read(KACK_HASH) },
          new JetArm[0],
          new JetHook[0],
          new ChildCore[] {
            new ChildCore("dec",
              Axis.CONTEXT,
              new BatteryHash[] { BatteryHash.read(DEC_HASH) },
              new JetArm[] {
                new AxisArm(Axis.HEAD, (ref, axis) ->
                    MockDecNodeGen.create(new SlotNode(Axis.SAMPLE)))
              },
              new JetHook[0],
              new ChildCore[0])})}));
  }

  @Before
  public void init() {
    MockDecNode.called = false;
  }

  private void doTest(Context context) {
    Value gate = context.eval(ackSource).execute();
    Value product = gate.getMetaObject().invokeMember("2", 2L, 2L);
    assertEquals(7L, product.as(Number.class));
  }

  @Test
  public void testUnjetted() {
    Context context = Context.create();
    doTest(context);
    assertFalse(MockDecNode.called);
    context.close();
  }
  
  @Test
  public void testOff() {
    Context context = Context.newBuilder("nock")
                             .option("nock.jets", "ack")
                             .option("nock.fast", "false")
                             .option("nock.hash", "false")
                             .build();
    doTest(context);
    assertFalse(MockDecNode.called);
    context.close();
  }

  @Test
  public void testFast() {
    Context context = Context.newBuilder("nock")
                             .option("nock.jets", "ack")
                             .option("nock.fast", "true")
                             .option("nock.hash", "false")
                             .build();
    doTest(context);
    assertTrue(MockDecNode.called);
    context.close();
  }

  @Test
  public void testHash() {
    Context context = Context.newBuilder("nock")
                             .option("nock.jets", "ack")
                             .option("nock.fast", "false")
                             .option("nock.hash", "true")
                             .build();
    doTest(context);
    assertTrue(MockDecNode.called);
    context.close();
  }

  @Test
  public void testOn() {
    Context context = Context.newBuilder("nock")
                             .option("nock.jets", "ack")
                             .option("nock.fast", "true")
                             .option("nock.hash", "true")
                             .build();
    doTest(context);
    assertTrue(MockDecNode.called);
    context.close();
  }
}
