package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.function.Function;
import java.util.function.BiFunction;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.PullHook;
import net.frodwith.jaque.runtime.NockContext;

// you can jet arms by name as long as there's a hook description
// and the hook is to pull an arm on the current core
public final class NameArm extends JetArm {
  private final String name;

  public NameArm(String name, BiFunction<AstContext,Axis,SubjectNode> factory) {
    super(factory);
    this.name = name;
  }

  @Override
  public Axis getAxis(Map<String,Hook> hooks) {
    Hook h = hooks.get(name);

    assert( h instanceof PullHook );
    PullHook ph = (PullHook) h;
    assert( Axis.IDENTITY == ph.toSubject );

    return ph.arm;
  }
}
