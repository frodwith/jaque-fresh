package net.frodwith.jaque.nodes.dispatch;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;

import com.oracle.truffle.api.CallTarget;

public abstract class HeadDispatchNode extends DispatchNode {
  @Specialization(limit = "2", guards = "target == cachedTarget")
  protected Object doDirect(CallTarget target, Object subject,
    @Cached("target") CallTarget cachedTarget,
    @Cached("new(target)") DirectDispatchNode directNode) {
    return directNode.executeDirect(subject);
  }

  @Specialization(replaces = "doDirect")
  protected Object doIndirect(CallTarget target, Object subject,
    @Cached("new()") IndirectDispatchNode indirectNode) {
    return indirectNode.executeIndirect(target, subject);
  }
}
