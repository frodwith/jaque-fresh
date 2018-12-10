package net.frodwith.jaque.test;

import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.oracle.truffle.api.dsl.NodeFactory;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.JetArm;
import net.frodwith.jaque.jet.AxisArm;
import net.frodwith.jaque.jet.JetHook;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.jet.ChildCore;
import net.frodwith.jaque.dashboard.FragHook;

/*
public RootCore(String name,
                Object payload,
                BatteryHash[] hashes,
                JetArm[] arms,
                JetHook[] hooks,
                ChildCore[] children) {
*/

public class JetParseTest {
  @Test
  public void testSimple() {
    JetTree tree = JetTree.parseOption("{:name \"kernel\" :payload 42}");
    assertEquals(1, tree.roots.length);
    RootCore root = tree.roots[0];
    assertEquals("kernel", root.name);
    assertEquals(42L, root.payload);
    assertEquals(0, root.hashes.length);
    assertEquals(0, root.arms.length);
    assertEquals(0, root.hooks.length);
    assertEquals(0, root.children.length);
  }

  @Test
  public void testComplex() {
    String hash1 =
      "d2ac125e597591ab376fe36b6101d04d24467c5fdaf6b3c679a3a3ed833e333a",
           hash2 =
      "9d12e0e0d05d03ff430f8a060619f9cea81367c94e97b13514ba111772e5cf15";

    String edn = 
      "{:name \"kernel\"\n" +
      " :payload 42\n" +
      " :hashes (\"" + hash1 + "\" \"" + hash2 + "\")\n" +
      " :hooks ({:name \"version\" :fragment 3})\n" +
      " :children (\n" +
      "   {:name \"dec\" :parent 3 :arms (\n" +
      "     {:arm 2 :class \"net.frodwith.jaque.nodes.jet.DecNodeFactory\"})})}";
    JetTree tree = JetTree.parseOption(edn);
    assertEquals(1, tree.roots.length);
    RootCore root = tree.roots[0];
    assertEquals("kernel", root.name);
    assertEquals(42L, root.payload);
    assertEquals(2, root.hashes.length);
    assertEquals(hash1, root.hashes[0].toString());
    assertEquals(hash2, root.hashes[1].toString());
    assertEquals(0, root.arms.length);
    assertEquals(1, root.hooks.length);
    JetHook h = root.hooks[0];
    assertEquals(h.name, "version");
    assertTrue(h.hook instanceof FragHook);
    assertEquals(Axis.TAIL, ((FragHook) h.hook).axis);
    assertEquals(1, root.children.length);
    ChildCore child = root.children[0];
    assertEquals(child.name, "dec");
    assertEquals(Axis.TAIL, child.toParent);
    assertEquals(0, child.hashes.length);
    assertEquals(1, child.arms.length);
    JetArm arm = child.arms[0];
    assertTrue(arm instanceof AxisArm);
    AxisArm aarm = (AxisArm) arm;
    assertEquals(Axis.HEAD, aarm.getAxis(new HashMap<>()));
    assertTrue(aarm.factory instanceof NodeFactory);
  }

  @Test
  public void testFile() {
    // XX TODO
    assumeTrue(false);
    assertTrue(false);
  }
}
