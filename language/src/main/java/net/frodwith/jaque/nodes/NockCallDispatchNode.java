package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.exception.NockControlFlowException;

// Note that this is not where we resolve cells to NockFunctions, but where
// we dispatch functions that have already been resolved.
//
public abstract class NockCallDispatchNode extends NockNode {
  public static final int INLINE_CACHE_SIZE = 3;

  public abstract Object executeCall(NockCall call);
  public final BranchProfile controlFlow = BranchProfile.create();
  public final ConditionProfile sameTarget = ConditionProfile.createBinaryProfile();

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "call.function == cachedFunction")
  protected Object doDirect(NockCall call,
      @Cached("call.function") NockFunction cachedFunction,
      @Cached("create(cachedFunction.callTarget)") DirectCallNode callNode) {
    NockFunction function = call.function;
    Object subject = call.subject;
    while ( true ) {
      try {
        return callNode.call(new Object[] { subject });
      }
      catch ( NockControlFlowException e ) {
        controlFlow.enter();
        subject = e.call.subject;
        if ( sameTarget.profile(e.call.function != function) ) {
          return executeCall(e.call);
        }
      }
    }
  }

  @Specialization(replaces = "doDirect")
  protected Object doIndirect(NockCall call,
      @Cached("create()") IndirectCallNode callNode) {
    NockFunction function = call.function;
    Object subject = call.subject;
    while ( true ) {
      try {
        return callNode.call(function.callTarget, new Object[] { subject });
      }
      catch ( NockControlFlowException e ) {
        subject  = e.call.subject;
        function = e.call.function;
      }
    }
  }
}
