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
import net.frodwith.jaque.nodes.jet.CanNodeGen;
import net.frodwith.jaque.nodes.jet.CapNodeGen;
import net.frodwith.jaque.nodes.jet.CatNodeGen;
import net.frodwith.jaque.nodes.jet.ConNodeGen;
import net.frodwith.jaque.nodes.jet.CueNodeGen;
import net.frodwith.jaque.nodes.jet.CutNodeGen;
import net.frodwith.jaque.nodes.jet.DecNodeGen;
import net.frodwith.jaque.nodes.jet.DisNodeGen;
import net.frodwith.jaque.nodes.jet.DivNodeGen;
import net.frodwith.jaque.nodes.jet.DorNodeGen;
import net.frodwith.jaque.nodes.jet.GorNodeGen;
import net.frodwith.jaque.nodes.jet.EndNodeGen;
import net.frodwith.jaque.nodes.jet.JamNodeGen;
import net.frodwith.jaque.nodes.jet.LossNodeGen;
import net.frodwith.jaque.nodes.jet.LoreNodeGen;
import net.frodwith.jaque.nodes.jet.LshNodeGen;
import net.frodwith.jaque.nodes.jet.LteNodeGen;
import net.frodwith.jaque.nodes.jet.LthNodeGen;
import net.frodwith.jaque.nodes.jet.MasNodeGen;
import net.frodwith.jaque.nodes.jet.MetNodeGen;
//import net.frodwith.jaque.nodes.jet.MinkNodeGen;
import net.frodwith.jaque.nodes.jet.MixNodeGen;
import net.frodwith.jaque.nodes.jet.ModNodeGen;
import net.frodwith.jaque.nodes.jet.MorNodeGen;
import net.frodwith.jaque.nodes.jet.MugNodeGen;
import net.frodwith.jaque.nodes.jet.MulNodeGen;
import net.frodwith.jaque.nodes.jet.PegNodeGen;
import net.frodwith.jaque.nodes.jet.RapNodeGen;
import net.frodwith.jaque.nodes.jet.RepNodeGen;
import net.frodwith.jaque.nodes.jet.RipNodeGen;
import net.frodwith.jaque.nodes.jet.RshNodeGen;
import net.frodwith.jaque.nodes.jet.ShalNodeGen;
import net.frodwith.jaque.nodes.jet.ShanNodeGen;
import net.frodwith.jaque.nodes.jet.ShayNodeGen;
import net.frodwith.jaque.nodes.jet.SubNodeGen;
import net.frodwith.jaque.nodes.jet.TripNodeGen;

import net.frodwith.jaque.nodes.jet.ut.CropNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FishNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FondNodeGen;
import net.frodwith.jaque.nodes.jet.ut.FuseNodeGen;
import net.frodwith.jaque.nodes.jet.ut.MintNodeGen;
import net.frodwith.jaque.nodes.jet.ut.MullNodeGen;
import net.frodwith.jaque.nodes.jet.ut.NestNodeGen;
import net.frodwith.jaque.nodes.jet.ut.PeekNodeGen;
import net.frodwith.jaque.nodes.jet.ut.PlayNodeGen;
import net.frodwith.jaque.nodes.jet.ut.RestNodeGen;

import net.frodwith.jaque.nodes.jet.crypto.EdPuckNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdSharNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdSignNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdVeriNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdPointAddNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdScalarmultNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdScalarmultBaseNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdAddScalarmultScalarmultBaseNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.EdAddDoubleScalarmultNodeGen;

import javax.crypto.Cipher;
import net.frodwith.jaque.nodes.jet.crypto.AesCbcNodeGen;
import net.frodwith.jaque.nodes.jet.crypto.AesEcbNodeGen;

/**
 * A jet tree which represents the vital numeric jets in arvo
 */
public class ArvoJetDashboard {
  private static ChildCore
  gate(String name, BiFunction<AstContext,Axis,SubjectNode> factory) {
    return offsetGate(name, Axis.CONTEXT, factory);
  }

  private static ChildCore
  offsetGate(String name,
             Axis toParent,
             BiFunction<AstContext,Axis,SubjectNode> factory) {
    return new ChildCore(name,
                         toParent,
                         new HashCode[0],
                         new JetArm[] { new AxisArm(Axis.HEAD, factory) },
                         new JetHook[0],
                         new ChildCore[0]);
  }

  private static ChildCore
  aesEcbCore(String name, int keysize) {
    return new ChildCore(name,
                         Axis.CONTEXT,
                         new HashCode[0],
                         new JetArm[0],
                         new JetHook[0],
                         new ChildCore[] {
                           gate("en", (c, cx) -> AesEcbNodeGen.create(
                               new SlotNode(Axis.CON_SAM),
                               new SlotNode(Axis.SAMPLE),
                               Cipher.ENCRYPT_MODE,
                               keysize)),
                           gate("de", (c, cx) -> AesEcbNodeGen.create(
                               new SlotNode(Axis.CON_SAM),
                               new SlotNode(Axis.SAMPLE),
                               Cipher.DECRYPT_MODE,
                               keysize)),
                         });
  }

  private static ChildCore
  aesCbcCore(String name, int keysize) {
    return new ChildCore(name,
                         Axis.CONTEXT,
                         new HashCode[0],
                         new JetArm[0],
                         new JetHook[0],
                         new ChildCore[] {
                           gate("en", (c, cx) -> AesCbcNodeGen.create(
                               new SlotNode(Axis.get(61L)),
                               new SlotNode(Axis.get(60L)),
                               new SlotNode(Axis.SAMPLE),
                               Cipher.ENCRYPT_MODE,
                               keysize)),
                           gate("de", (c, cx) -> AesCbcNodeGen.create(
                               new SlotNode(Axis.get(61L)),
                               new SlotNode(Axis.get(60L)),
                               new SlotNode(Axis.SAMPLE),
                               Cipher.DECRYPT_MODE,
                               keysize)),
                         });
  }

  private static JetHook pullHook(String name, long axis) {
    return new JetHook(name, new PullHook(Axis.IDENTITY, Axis.get(axis)));
  }

  private static JetHook fragHook(String name, long axis) {
    return new JetHook(name, new FragHook(Axis.get(axis)));
  }

  // While +nest:ut is a gate, what we perform matching on is actually a
  // subcore two hints down.
  private static final ChildCore jetUtNestCore =
      new ChildCore(
          "nest",
          Axis.CONTEXT,
          new HashCode[0],
          new JetArm[0],
          new JetHook[0],
          new ChildCore[] { new ChildCore(
              "nest-in",
              Axis.CONTEXT,
              new HashCode[0],
              new JetArm[0],
              new JetHook[0],
              new ChildCore[] { new ChildCore(
                  "nest-dext",
                  Axis.TAIL,
                  new HashCode[0],
                  new JetArm[] { new AxisArm(
                      Axis.HEAD,
                      (c, ax) ->
                      NestNodeGen.create(
                          // cor
                          new SlotNode(Axis.IDENTITY),
                          // seg / (peg u3x_pay u3x_sam_2)
                          new SlotNode(Axis.get(28L)),
                          // reg / (peg u3x_pay u3x_sam_6)
                          new SlotNode(Axis.get(58L)),
                          // ref / (peg (peg u3x_pay u3x_con) u3x_sam_3)
                          new SlotNode(Axis.get(125L)),
                          // van_van / :(peg u3x_pay u3x_con u3x_con u3qfu_van_vet)
                          new SlotNode(Axis.get(4_086L)),
                          // sut / :(peg u3x_pay u3x_con u3x_con u3x_sam)
                          new SlotNode(Axis.get(254L)),
                          c))},
                  new JetHook[0],
                  new ChildCore[0])})});

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

                      // Matching +nest requires matching a tree of nested cores.
                      jetUtNestCore,

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

  private static final ChildCore edCore =
      new ChildCore("ed",
                    Axis.TAIL,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      gate("point-add", (c, ax) ->
                           EdPointAddNodeGen.create(new SlotNode(Axis.SAM_2),
                                                    new SlotNode(Axis.SAM_3))),
                      gate("scalarmult", (c, ax) ->
                           EdScalarmultNodeGen.create(
                               new SlotNode(Axis.SAM_2),
                               new SlotNode(Axis.SAM_3))),
                      gate("scalarmult-base", (c, ax) ->
                           EdScalarmultBaseNodeGen.create(
                               new SlotNode(Axis.SAMPLE))),
                      gate("add-scalarmult-scalarmult-base", (c, ax) ->
                           EdAddScalarmultScalarmultBaseNodeGen.create(
                               new SlotNode(Axis.SAM_2),
                               new SlotNode(Axis.SAM_6),
                               new SlotNode(Axis.SAM_7))),
                      gate("add-double-scalarmult", (c, ax) ->
                           EdAddDoubleScalarmultNodeGen.create(
                               new SlotNode(Axis.SAM_2),
                               new SlotNode(Axis.SAM_6),
                               new SlotNode(Axis.SAM_14),
                               new SlotNode(Axis.SAM_15))),

                      gate("puck", (c, ax) ->
                           EdPuckNodeGen.create(new SlotNode(Axis.SAMPLE))),
                      gate("shar", (c, ax) ->
                           EdSharNodeGen.create(new SlotNode(Axis.SAM_2),
                                                new SlotNode(Axis.SAM_3))),
                      gate("sign", (c, ax) ->
                           EdSignNodeGen.create(new SlotNode(Axis.SAM_2),
                                                new SlotNode(Axis.SAM_3))),
                      gate("veri", (c, ax) ->
                           EdVeriNodeGen.create(new SlotNode(Axis.SAM_2),
                                                new SlotNode(Axis.SAM_6),
                                                new SlotNode(Axis.SAM_7))),
                    });

  private static final ChildCore coedCore =
      new ChildCore("coed",
                    Axis.get(63L),
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      edCore
                    });

  private static final ChildCore aesCore =
      new ChildCore("aes",
                    Axis.get(31L),
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      aesEcbCore("ecba", 16),
                      aesEcbCore("ecbb", 24),
                      aesEcbCore("ecbc", 32),
                      aesCbcCore("cbca", 16),
                      aesCbcCore("cbcb", 24),
                      aesCbcCore("cbcc", 32),
                    });

  private static final ChildCore hexLayer =
      new ChildCore("hex",
                    Axis.CONTEXT,
                    new HashCode[0],
                    new JetArm[0],
                    new JetHook[0],
                    new ChildCore[] {
                      // TODO: I'm unsure about the jet matching here. Or: Is
                      // this loss?
                      //
                      offsetGate("loss", Axis.get(63L),
                                 (c, ax) -> LossNodeGen.create(
                                     new SlotNode(Axis.SAM_2),
                                     new SlotNode(Axis.SAM_3))),
                      offsetGate("lore", Axis.get(63L),
                                 (c, ax) -> LoreNodeGen.create(
                                     new SlotNode(Axis.SAMPLE))),

                      coedCore,
                      aesCore
                    });

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
                      hexLayer,
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
                      gate("shay", (c, ax) ->
                           ShayNodeGen.create(new SlotNode(Axis.SAM_2),
                                              new SlotNode(Axis.SAM_3))),

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
                      gate("can", (c, ax) ->
                           CanNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
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
                      gate("rap", (c, ax) ->
                           RapNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("rep", (c, ax) ->
                           RepNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
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
                      gate("gor", (c, ax) ->
                           GorNodeGen.create(new SlotNode(Axis.SAM_2),
                                             new SlotNode(Axis.SAM_3))),
                      gate("mor", (c, ax) ->
                           MorNodeGen.create(new SlotNode(Axis.SAM_2),
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
