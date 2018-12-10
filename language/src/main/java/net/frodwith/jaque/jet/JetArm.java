package net.frodwith.jaque.jet;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;

import us.bpsm.edn.Keyword;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.JetRootNode;

public abstract class JetArm {
  public Class<? extends SubjectNode> klass;
  private static final Keyword classKey = Keyword.newKeyword("class"),
                               nameKey  = Keyword.newKeyword("name"),
                               axisKey  = Keyword.newKeyword("axis");

  protected JetArm(Class<? extends JetNode> klass) {
    this.klass = klass;
  }

  public abstract Axis getAxis(Map<String,Hook> hooks);

  public final NockFunction getFunction(NockLanguage language) {
    final JetRootNode root = new JetRootNode(language, NockLanguage.DESCRIPTOR,
          factory.createNode(factory.createInputNodes()));
    RootCallTarget target = Truffle.getRuntime().createCallTarget(root);
    return new NockFunction(target);
  }

  public static final JetArm parseOption(Object option) {
    Map<?,?> m = (Map<?,?>) option;
    String className = (String) m.get(classKey);

    Class<? extends SubjectNode> klass =
      Class.forName(className).asSubclass(SubjectNode.class);

    Object axOb = m.get(axisKey);

    return ( null == axOb )
      ? new NameArm((String) m.get(nameKey), klass)
      : new AxisArm(Axis.parseOption(axOb), klass);
  }
}
