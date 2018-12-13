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
::  0xf7dc.a0db.495c.7ef2.0322.7c92.e0b9.93ff.bf9f.7695.3702.c927.42a7.aede.ba43.c8a9
::  0x8bd3.ab82.1878.69ab.35ac.9293.df0a.4753.f612.6c4f.4973.7288.533a.d51d.d114.2fe6
::  =-  [`@ux`(shax (jam -:ack)) `@ux`(shax (jam +>-:ack))]
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

  private static final String KACK_HASH =
    "8bd3ab82187869ab35ac9293df0a4753f6126c4f49737288533ad51dd1142fe6";

  private static final String DEC_HASH =
    "f7dca0db495c7ef203227c92e0b993ffbf9f76953702c92742a7aedeba43c8a9";

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
  public void testSlow() {
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
  public void testHashed() {
    Context context = Context.newBuilder("nock")
                             .option("nock.jets", "ackHash")
                             .option("nock.fast", "false")
                             .option("nock.hash", "true")
                             .build();
    doTest(context);
    assertTrue(MockDecNode.called);
    context.close();
  }
}
