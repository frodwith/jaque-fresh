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
import net.frodwith.jaque.dashboard.FragHook;
import net.frodwith.jaque.dashboard.PullHook;

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
//import net.frodwith.jaque.nodes.jet.MinkNodeGen;
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
import net.frodwith.jaque.nodes.jet.TripNodeGen;

import net.frodwith.jaque.nodes.jet.ut.CropNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FishNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FondNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FuseNodeGen;
import net.frodwith.jaque.nodes.jet.ut.MintNodeGen;
import net.frodwith.jaque.nodes.jet.ut.MullNodeGen;
import net.frodwith.jaque.nodes.jet.ut.PeekNodeGen;
import net.frodwith.jaque.nodes.jet.ut.PlayNodeGen;
import net.frodwith.jaque.nodes.jet.ut.RestNodeGen;

//import net.frodwith.jaque.nodes.jet.crypto.EdPuckNodeGen;

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

  private static JetHook pullHook(String name, long axis) {
    return new JetHook(name, new PullHook(Axis.IDENTITY, Axis.get(axis)));
  }

  private static JetHook fragHook(String name, long axis) {
    return new JetHook(name, new FragHook(Axis.get(axis)));
  }

  private static final ChildCore jetUtCore =
      new ChildCore("ut",
                    Axis.get(15L),
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[] {
                      pullHook("ar", 12282L),
                      fragHook("fan", 28L),
                      fragHook("rib", 58L),
                      fragHook("vet", 118L),
                      fragHook("fab", 119L),
                      pullHook("blow",    6015L),
                      pullHook("burp",     342L),
                      pullHook("busk",    1373L),
                      pullHook("buss",     374L),
                      pullHook("crop",    1494L),
                      pullHook("duck",    1524L),
                      pullHook("dune",    5982L),
                      pullHook("dunk",    3066L),
                      pullHook("epla",   12206L),
                      pullHook("emin",    1534L),
                      pullHook("emul",    6134L),
                      pullHook("feel",    1502L),
                      pullHook("felt",      94L),
                      pullHook("fine",   49086L),
                      pullHook("fire",       4L),
                      pullHook("fish",    6006L),
                      pullHook("fond",   12283L),
                      pullHook("fund",    6014L),
                      //  XX +funk is not part of +ut, and this hook appears to be unused
                      //  remove from here and the +ut hint
                      new JetHook("funk", new PullHook(Axis.get(31L), Axis.get(0xbefafaL))),
                      pullHook("fuse",   24021L),
                      pullHook("gain",     380L),
                      pullHook("lose", 0x2fefeL),
                      pullHook("mile",     382L),
                      pullHook("mine",     372L),
                      pullHook("mint",   49083L),
                      pullHook("moot", 0x2feffL),
                      pullHook("mull",   24020L),
                      pullHook("nest",      92L),
                      pullHook("peel",    1526L),
                      pullHook("play",    3006L),
                      pullHook("peek",    1532L),
                      pullHook("repo",      22L),
                      pullHook("rest",    6102L),
                      pullHook("tack",    6007L),
                      pullHook("toss",   24540L),
                      pullHook("wrap",    6140L),

                    },
                    new ChildCore[] {
                      gate("crop", (c, ax) ->
                           CropNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAMPLE),   // ref
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("fish", (c, ax) ->
                           FishNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAMPLE),   // axe
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("fond", (c, ax) ->
                           FondNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAM_2),    // way
                                              new SlotNode(Axis.SAM_3),    // hyp
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("fuse", (c, ax) ->
                           FuseNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAMPLE),   // ref
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("mint", (c, ax) ->
                           MintNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAM_2), // gol
                                              new SlotNode(Axis.SAM_3), // gen
                                              // vrf / (peg u3x_con u3qfu_van_vrf)
                                              new SlotNode(Axis.get(251L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("mull", (c, ax) ->
                           MullNodeGen.create(new SlotNode(Axis.IDENTITY),  // cor
                                              new SlotNode(Axis.SAM_2),     // gol
                                              new SlotNode(Axis.SAM_6),     // dox
                                              new SlotNode(Axis.SAM_7),     // gen
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sut / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),

                      // +nest is not straightforward and isn't a copypasta of
                      // DecapitatedJetNode.

                      gate("peek", (c, ax) ->
                           PeekNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAM_2),    // way
                                              new SlotNode(Axis.SAM_3),    // hyp
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("play", (c, ax) ->
                           PlayNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAMPLE),  // gen
                                              // vrf / (peg u3x_con u3qfu_van_vrf)
                                              new SlotNode(Axis.get(251L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                      gate("rest", (c, ax) ->
                           RestNodeGen.create(new SlotNode(Axis.IDENTITY), // cor
                                              new SlotNode(Axis.SAMPLE),  // gen
                                              // vet / (peg u3x_con u3qfu_van_vet)
                                              new SlotNode(Axis.get(502L)),
                                              // sam / (peg u3x_con u3x_sam)
                                              new SlotNode(Axis.get(30L)),
                                              c)),
                    });

  // private static final ChildCore edCore =
  //     new ChildCore("ed",
  //                   Axis.TAIL,
  //                   new HashCode[0],
  //                   new JetArm[0],
  //                   new JetHook[0],
  //                   new ChildCore[] {
  //                     gate("puck", (c, ax) ->
  //                          EdPuckNodeGen.create(new SlotNode(Axis.SAMPLE))),
  //                   });

  // ???
  //
  // [nock::Dashboard] WARNING: trying to register coed with unlocated parent.
  // [nock::Dashboard] WARNING: trying to register ed with unlocated parent.
  // [nock::Dashboard] WARNING: trying to register puck with unlocated parent.
  // private static final ChildCore coedCore =
  //     new ChildCore("coed",
  //                   Axis.get(63L),
  //                   new HashCode[0],
  //                   new JetArm[0],
  //                   new JetHook[0],
  //                   new ChildCore[] {
  //                     edCore
  //                   });

  // private static final ChildCore hexLayer =
  //     new ChildCore("hex",
  //                   Axis.CONTEXT,
  //                   new HashCode[0],
  //                   new JetArm[0],
  //                   new JetHook[0],
  //                   new ChildCore[] {
  //                     coedCore
  //                   });


  private static final ChildCore jetLayerFive =
      new ChildCore("pen",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[] {
                      pullHook("ap", 86L),
                      pullHook("ut", 342L),
                    },
                    new ChildCore[] {
                      //                      hexLayer,
                      jetUtCore
                    });

  private static final ChildCore jetLayerFour =
      new ChildCore("qua",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[] {
                      pullHook("mute", 0x2fbabeL),
                      pullHook("show", 24406L),
                    },
                    new ChildCore[] {
                      gate("trip", (c, ax) ->
                           TripNodeGen.create(new SlotNode(Axis.SAMPLE))),

                      // gate("mink", (c, ax) ->
                      //      MinkNodeGen.create(new SlotNode(Axis.SAM_4),
                      //                         new SlotNode(Axis.SAM_5),
                      //                         new SlotNode(Axis.SAM_3))),

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
