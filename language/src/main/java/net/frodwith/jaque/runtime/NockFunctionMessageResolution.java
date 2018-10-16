package net.frodwith.jaque.runtime;

import static net.frodwith.jaque.runtime.NockContext.fromForeignValue;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.nodes.*;

@MessageResolution(receiverType = NockFunction.class)
public class NockFunctionMessageResolution {

  @Resolve(message = "EXECUTE")
  public abstract static class NockForeignFunctionExecuteNode extends Node {
    @Child private NockFunctionDispatchNode dispatch =
      NockFunctionDispatchNodeGen.create();

    public Object access(NockFunction function, Object[] arguments) {
      Object subject;
      switch ( arguments.length ) {
        case 0:
          subject = 0L;
          break;
        case 1:
          subject = fromForeignValue(arguments[0]);
          break;
        default:
          throw ArityException.raise(1, arguments.length);
      }
      return dispatch.executeFunction(function, subject);
    }
  }

  @Resolve(message = "IS_EXECUTABLE")
  public abstract static class NockForeignIsExecutableNode extends Node {
    public Object access(Object receiver) {
      return receiver instanceof NockFunction;
    }
  }

  @CanResolve
  public abstract static class CheckNockFunction extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof NockFunction;
    }
  }
}
