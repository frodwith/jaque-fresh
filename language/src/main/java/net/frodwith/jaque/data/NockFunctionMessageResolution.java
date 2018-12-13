package net.frodwith.jaque.data;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.nodes.Node;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;

@MessageResolution(receiverType = NockFunction.class)
public class NockFunctionMessageResolution {

  @Resolve(message = "EXECUTE")
  public abstract static class NockForeignFunctionExecuteNode extends Node {
    @Child private NockCallDispatchNode dispatch =
      NockCallDispatchNodeGen.create();

    public Object access(NockFunction function, Object[] arguments) {
      Object subject = NockLanguage.fromArguments(arguments, 0L);
      return dispatch.executeCall(new NockCall(function, subject));
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
