package net.frodwith.jaque.jet;

import java.util.Map;

import com.oracle.truffle.api.dsl.NodeFactory;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.nodes.SubjectNode;

public abstract class JetArm {
  private NodeFactory<? extends SubjectNode> factory;

  protected JetArm(NodeFactory<? extends SubjectNode> factory) {
    this.factory = factory;
  }

  public abstract Axis getAxis(Map<String,Hook> hooks);

  public final NockFunction getFunction(NockLanguage language) {
    final JetRootNode root = new JetRootNode(language, 
                                             NockLanguage.DESCRIPTOR,
                                             factory.createNode());
    RootCallTarget target = Truffle.getRuntime().createCallTarget(root);
    return new NockFunction(target);
  }
}
