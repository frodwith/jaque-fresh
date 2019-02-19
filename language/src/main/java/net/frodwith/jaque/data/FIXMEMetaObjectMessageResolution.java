package net.frodwith.jaque.data;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;
import net.frodwith.jaque.parser.SimpleAtomParser;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@MessageResolution(receiverType = FIXMEMetaObject.class)
public abstract class FIXMEMetaObjectMessageResolution extends Node {

  @Resolve(message = "READ")
  public abstract static class FIXMEMetaObjectReadNode extends Node {
    protected Object access(FIXMEMetaObject reciever, String name) {
      if ( name.equals("isCore") ) {
        return reciever.hasObject();
      }
      else {
        throw UnknownIdentifierException.raise(name);
      }
    }
  }

  @Resolve(message = "EXECUTE")
  public abstract static class FIXMEMetaObjectExecuteNode extends Node {
    @Child private NockCallDispatchNode dispatch =
      NockCallDispatchNodeGen.create();

    protected Object access(FIXMEMetaObject reciever, Object[] arguments) {
      NockFunction function;
      Object subject = NockLanguage.fromArguments(arguments, 0L);
      try {
        function = reciever.getFunction();
      }
      catch ( ExitException e ) {
        throw UnsupportedMessageException.raise(Message.EXECUTE);
      }
      return dispatch.executeCall(new NockCall(function, subject));
    }
  }

  @Resolve(message = "INVOKE")
	public abstract static class FIXMEMetaObjectInvokeNode extends Node {
		@Child private NockCallDispatchNode dispatch = NockCallDispatchNodeGen.create();

    private Object accessZero(FIXMEMetaObject receiver, String name) {
      NockCall call;
      try {
        call = receiver.getCall(Axis.require(SimpleAtomParser.parse(name)));
      }
      catch ( ExitException e ) {
        throw UnknownIdentifierException.raise(name);
      }
      return dispatch.executeCall(call);
    }

    public Object access(FIXMEMetaObject receiver, String name, Object[] arguments) {
      if ( 0 == arguments.length ) {
        return accessZero(receiver, name);
      }
      try {
        Object sample = NockLanguage.fromArguments(arguments);
        FIXMEMetaObject newMeta = receiver.edit(Axis.SAMPLE, sample);
        return accessZero(newMeta, name);
      }
      catch ( ExitException e ) {
        throw UnsupportedMessageException.raise(Message.INVOKE);
      }
    }
  }

  @CanResolve
  public abstract static class FIXMEMetaObjectCheckNode extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof FIXMEMetaObject;
    }
  }
}
