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
import net.frodwith.jaque.nodes.jet.BexNodeGen;
import net.frodwith.jaque.nodes.jet.CapNodeGen;
import net.frodwith.jaque.nodes.jet.CatNodeGen;
import net.frodwith.jaque.nodes.jet.ConNodeGen;
import net.frodwith.jaque.nodes.jet.CueNodeGen;
import net.frodwith.jaque.nodes.jet.CutNodeGen;
import net.frodwith.jaque.nodes.jet.DecNodeGen;
import net.frodwith.jaque.nodes.jet.DisNodeGen;
import net.frodwith.jaque.nodes.jet.DivNodeGen;
import net.frodwith.jaque.nodes.jet.DorNodeGen;
import net.frodwith.jaque.nodes.jet.EndNodeGen;
import net.frodwith.jaque.nodes.jet.JamNodeGen;
import net.frodwith.jaque.nodes.jet.LshNodeGen;
import net.frodwith.jaque.nodes.jet.LteNodeGen;
import net.frodwith.jaque.nodes.jet.LthNodeGen;
import net.frodwith.jaque.nodes.jet.MasNodeGen;
import net.frodwith.jaque.nodes.jet.MetNodeGen;
import net.frodwith.jaque.nodes.jet.MixNodeGen;
import net.frodwith.jaque.nodes.jet.ModNodeGen;
import net.frodwith.jaque.nodes.jet.MugNodeGen;
import net.frodwith.jaque.nodes.jet.MulNodeGen;
import net.frodwith.jaque.nodes.jet.PegNodeGen;
import net.frodwith.jaque.nodes.jet.RipNodeGen;
import net.frodwith.jaque.nodes.jet.RshNodeGen;
import net.frodwith.jaque.nodes.jet.ShalNodeGen;
import net.frodwith.jaque.nodes.jet.ShanNodeGen;
import net.frodwith.jaque.nodes.jet.SubNodeGen;

import net.frodwith.jaque.nodes.jet.ut.MintNodeGen;

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

  private static final ChildCore jetUtCore =
      new ChildCore("ut",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("mint", (c, ax) ->
                           MintNodeGen.create(new SlotNode(Axis.IDENTITY),
                                              new SlotNode(Axis.SAM_2),
                                              new SlotNode(Axis.SAM_3),
                                              new SlotNode(Axis.CONTEXT))),
                    });

  private static final ChildCore jetLayerFive =
      new ChildCore("pen",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      jetUtCore
                    });

  private static final ChildCore jetLayerFour =
      new ChildCore("qua",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      jetLayerFive
                    });

  private static final ChildCore jetLayerThree =
      new ChildCore("tri",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("shal", (c, ax) ->
                           ShalNodeGen.create(new SlotNode(Axis.SAM_2),
                                              new SlotNode(Axis.SAM_3))),
                      gate("shan", (c, ax) ->
                           ShanNodeGen.create(new SlotNode(Axis.SAMPLE))),

                      jetLayerFour
                    });

  private static final ChildCore jetLayerTwo =
      new ChildCore("two",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("bex", (c, ax) ->
                           BexNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      // +can: No math implementation of it yet.
                      gate("cat", (c, ax) ->
                           CatNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_6),
                                             new SlotNode(Axis.SAM_7))),
                      gate("cut", (c, ax) ->
                           CutNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_12),
                                             new SlotNode(Axis.SAM_13),
                                             new SlotNode(Axis.SAM_7))),
                      gate("end", (c, ax) ->
                           EndNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_6),
                                             new SlotNode(Axis.SAM_7))),
                      gate("lsh", (c, ax) ->
                           LshNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_6),
                                             new SlotNode(Axis.SAM_7))),
                      gate("met", (c, ax) ->
                           MetNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      // +rap: no math impl
                      // +rep: no math impl
                      gate("rip", (c, ax) ->
                           RipNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("rsh", (c, ax) ->
                           RshNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_6),
                                             new SlotNode(Axis.SAM_7))),
                      gate("con", (c, ax) ->
                           ConNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("dis", (c, ax) ->
                           DisNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("mix", (c, ax) ->
                           MixNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),

                      gate("mug", (c, ax) ->
                          MugNodeGen.create(new SlotNode(Axis.SAMPLE))),

                      gate("dor", (c, ax) ->
                           DorNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),

                      // ???????

                      gate("jam", (c, ax) ->
                           JamNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("cue", (c, ax) ->
                           CueNodeGen.create(new SlotNode(Axis.SAMPLE))),

                      jetLayerThree
                    });


  private static final ChildCore jetLayerOne =
      new ChildCore("one",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("add", (c, ax) ->
                           AddNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("dec", (c, ax) ->
                           DecNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("div", (c, ax) ->
                           DivNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      // Skipping +dvr since it's just a call to div and mod.
                      // +gte is just !lth
                      // +gth is just !lte
                      gate("lte", (c, ax) ->
                           LteNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("lth", (c, ax) ->
                           LthNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      // +max is just a call to +gth
                      // +min is just a call to +lth
                      gate("mod", (c, ax) ->
                           ModNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("mul", (c, ax) ->
                           MulNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("sub", (c, ax) ->
                           SubNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("cap", (c, ax) ->
                           CapNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("mas", (c, ax) ->
                           MasNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("peg", (c, ax) ->
                           PegNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      jetLayerTwo
                    });  //,

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
