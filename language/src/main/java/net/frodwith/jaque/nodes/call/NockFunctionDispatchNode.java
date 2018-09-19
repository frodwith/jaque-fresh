package net.frodwith.jaque.nodes.call;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import com.oracle.truffle.api.profiles.BranchProfile;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.exception.NockControlFlowException;
import net.frodwith.jaque.nodes.NockTypes;

// Note that this is not where we resolve cells to NockFunctions, but where
// we dispatch functions that have already been resolved.
//
@ReportPolymorphism
@TypeSystemReference(NockTypes.class)
public abstract class NockFunctionDispatchNode extends Node {
  public static final int INLINE_CACHE_SIZE = 2;

  public abstract Object executeFunction(Object function, Object subject);
  public final BranchProfile loopProfile = BranchProfile.create();
  public final BranchProfile tailProfile = BranchProfile.create();

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "function == cachedFunction")
  protected Object doDirect(NockFunction function, Object subject,
      @Cached("function") NockFunction cachedFunction,
      @Cached("create(cachedFunction.callTarget)") DirectCallNode callNode) {
    while ( true ) {
      try {
        return callNode.call(new Object[] { subject });
      }
      catch ( NockControlFlowException e ) {
        subject = e.subject;
        if ( e.function == function ) {
          loopProfile.enter();
        }
        else {
          tailProfile.enter();
          return executeFunction(e.function, subject);
        }
      }
    }
  }

  @Specialization(replaces = "doDirect")
  protected Object doIndirect(NockFunction function, Object subject,
      @Cached("create()") IndirectCallNode callNode) {
    while ( true ) {
      try {
        return callNode.call(function.callTarget, new Object[] { subject });
      }
      catch ( NockControlFlowException e ) {
        subject  = e.subject;
        function = e.function;
      }
    }
  }
}
