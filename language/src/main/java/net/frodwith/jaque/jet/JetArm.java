package net.frodwith.jaque.jet;

import java.util.function.BiFunction;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import us.bpsm.edn.Keyword;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.JetRootNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.NockContext;

public abstract class JetArm {
  public BiFunction<ContextReference<NockContext>, Axis, SubjectNode> factory;

  protected JetArm(
    BiFunction<ContextReference<NockContext>, Axis, SubjectNode> factory) {
    this.factory = factory;
  }

  public abstract Axis getAxis(Map<String,Hook> hooks);

  public final NockFunction getFunction(NockLanguage language, 
                                        ContextReference<NockContext> ref,
                                        Axis axis) {
    SubjectNode node = factory.apply(ref, axis);
    JetRootNode root = new JetRootNode(language, NockLanguage.DESCRIPTOR, node);
    RootCallTarget target = Truffle.getRuntime().createCallTarget(root);
    return new NockFunction(target);
  }
}
