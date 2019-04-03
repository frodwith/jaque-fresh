package net.frodwith.jaque.jet;

import java.util.function.Function;
import java.util.function.BiFunction;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.nodes.jet.JetNode;
import net.frodwith.jaque.nodes.JetRootNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.parser.FormulaParser;

public abstract class JetArm {
  public BiFunction<AstContext,Axis,SubjectNode> factory;

  protected JetArm(BiFunction<AstContext,Axis,SubjectNode> factory) {
    this.factory = factory;
  }

  public abstract Axis getAxis(Map<String,Hook> hooks);

  public final Function<AstContext,CallTarget> getFactory(Axis axis) {
    return (c) -> Truffle.getRuntime().createCallTarget(
      new JetRootNode(c.language, factory.apply(c, axis)));
  }
}
