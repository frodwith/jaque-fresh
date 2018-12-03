package net.frodwith.jaque.jet;

import java.util.Map;

import com.oracle.truffle.api.dsl.NodeFactory;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.PullHook;

// you can jet arms by name as long as there's a hook description
// and the hook is to pull an arm on the current core
public final class NameArm extends JetArm {
  private final String name;

  protected NameArm(String name, NodeFactory<SubjectNode> factory) {
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
