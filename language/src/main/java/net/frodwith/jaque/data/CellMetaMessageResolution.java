package net.frodwith.jaque.data;

import static net.frodwith.jaque.runtime.NockContext.fromForeignValue;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;
import net.frodwith.jaque.parser.SimpleAtomParser;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@MessageResolution(receiverType = CellMeta.class)
public abstract class CellMetaMessageResolution extends Node {

  protected static Object argsToNoun(Object[] arguments) {
    Object product = fromForeignValue(arguments[arguments.length-1]);
    for ( int i = arguments.length-2; i >= 0; --i ) {
      product = new Cell(fromForeignValue(arguments[i]), product);
    }
    return product;
  }

  @Resolve(message = "EXECUTE")
  public abstract static class CellExecuteNode extends Node {
    @Child private NockCallDispatchNode dispatch =
      NockCallDispatchNodeGen.create();

    protected Object access(CellMeta reciever, Object[] arguments) {
      Object subject;
      NockFunction function;
      if ( 0 == arguments.length ) {
        subject = 0L;
      }
      else {
        subject = argsToNoun(arguments);
      }
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
	public abstract static class CellInvokeNode extends Node {
		@Child private NockCallDispatchNode dispatch = NockCallDispatchNodeGen.create();

    private Object accessRaw(CellMeta receiver, String name) {
      Axis axis;
      NockFunction arm;

      try {
         axis = Axis.require(SimpleAtomParser.parse(name));
      }
      catch ( ExitException e ) {
        throw UnknownIdentifierException.raise(name);
      }

      if ( axis.inHead() ) {
        try {
          NockObject object = receiver.getObject();
          arm = object.getArm(axis, receiver.context);
        }
        catch ( ExitException e ) {
          throw UnsupportedMessageException.raise(Message.INVOKE);
        }
      }
      else {
        Object formula;
        try {
          formula = axis.fragment(receiver.cell);
        }
        catch ( ExitException e ) {
          throw UnknownIdentifierException.raise(name);
        }
        try {
          arm = Cell.require(formula).getMeta(receiver.context).getFunction();
        }
        catch ( ExitException e ) {
          throw UnsupportedMessageException.raise(Message.INVOKE);
        }
      }

      return dispatch.executeCall(new NockCall(arm, receiver.cell));
    }

    public Object access(CellMeta receiver, String name, Object[] arguments) {
      if ( 0 == arguments.length ) {
        return accessRaw(receiver, name);
      }
      try {
        Object subject   = Axis.SAMPLE.edit(receiver.cell, argsToNoun(arguments));
        CellMeta newMeta = Cell.require(subject).getMeta(receiver.context);
        return accessRaw(newMeta, name);
      }
      catch ( ExitException e ) {
        throw UnsupportedMessageException.raise(Message.INVOKE);
      }
    }
  }

  @CanResolve
  public abstract static class CheckCellMeta extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof CellMeta;
    }
  }
}
