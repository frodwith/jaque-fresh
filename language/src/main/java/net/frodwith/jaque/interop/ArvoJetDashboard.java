package net.frodwith.jaque.interop;

import java.util.function.BiFunction;

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
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.BatteryHash;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.jet.AddNodeGen;
import net.frodwith.jaque.nodes.jet.DecNodeGen;
import net.frodwith.jaque.nodes.jet.DivNodeGen;
import net.frodwith.jaque.nodes.jet.LteNodeGen;
import net.frodwith.jaque.nodes.jet.LthNodeGen;
import net.frodwith.jaque.nodes.jet.ModNodeGen;
import net.frodwith.jaque.nodes.jet.MulNodeGen;


/**
 * A jet tree which represents the vital numeric jets in arvo
 */
public class ArvoJetDashboard {
  private static ChildCore
    gate(String name, BiFunction<AstContext,Axis,SubjectNode> factory) {
    return new ChildCore(name,
        Axis.CONTEXT,
        new HashCode[0],
        new JetArm[] { new AxisArm(Axis.HEAD, factory) },
        new JetHook[0],
        new ChildCore[0]);
  }

  private static final ChildCore jetLayerOne =
      new ChildCore("one",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("add", (c, ax) ->
                           AddNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                      gate("dec", (c, ax) ->
                           DecNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("div", (c, ax) ->
                           DivNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                      // Skipping +dvr since it's just a call to div and mod.
                      // +gte is just !lth
                      // +gth is just !lte
                      gate("lte", (c, ax) ->
                           LteNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                      gate("lth", (c, ax) ->
                           LthNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                      // +max is just a call to +gth
                      // +min is just a call to +lth
                      gate("mod", (c, ax) ->
                           ModNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                      gate("mul", (c, ax) ->
                           MulNodeGen.create(new SlotNode(Axis.get(12L)),
                                             new SlotNode(Axis.get(13L)))),
                    });  //,
              // new ChildCore("two",
              //   Axis.TAIL,
              //   new HashCode[0],
              //   new JetArm[0],
              //   new JetHook[0],
              //   new ChildCore[] {
              //     gate("fib", (c, ax) ->
              //       new CountNockNode(c, ax, "fib"))})})})})

  private static final JetTree jetTree =
      new JetTree(new RootCore[] {
          new RootCore("k141", 141L,
                       new HashCode[0],
                       new JetArm[0],
                       new JetHook[0],
                       new ChildCore[] { jetLayerOne })});

  public static Dashboard build() {
    // TODO: Add hashes.
    return new Dashboard.Builder()
      .setFastHints(true)
      .setHashDiscovery(false)
      .setJetTree(jetTree)
      .build();
  }
}
