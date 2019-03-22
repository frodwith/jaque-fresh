package net.frodwith.jaque.jet;

import java.util.function.Function;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.JetRootNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.NockContext;

public abstract class JetArm {
  public Function<JetContext, SubjectNode> factory;

  protected JetArm(Function<JetContext, SubjectNode> factory) {
    this.factory = factory;
  }

  public abstract Axis getAxis(Map<String,Hook> hooks);

  public final RootCallTarget getFunction(JetContext jetContext) {
    SubjectNode node = factory.apply(jetContext);
    JetRootNode root = 
      new JetRootNode(jetContext.language, NockLanguage.DESCRIPTOR, node);
    return Truffle.getRuntime().createCallTarget(root);
  }
}
