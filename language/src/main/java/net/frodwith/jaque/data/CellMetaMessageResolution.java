package net.frodwith.jaque.data;

import static net.frodwith.jaque.runtime.NockContext.fromForeignValue;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.TruffleObject;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@MessageResolution(receiverType = CellMeta.class)
public abstract class CellMetaMessageResolution extends Node {
  @Resolve(message = "EXECUTE")
  public abstract static class CellExecuteNode extends Node {
    @Child private NockCallDispatchNode dispatch =
      NockCallDispatchNodeGen.create();

    protected Object access(CellMeta reciever, Object[] arguments) {
      Object subject;
      if ( 0 == arguments.length ) {
        subject = 0L;
      }
      else {
        subject = fromForeignValue(arguments[arguments.length-1]);
        for ( int i = arguments.length-2; i >= 0; --i ) {
          subject = new Cell(fromForeignValue(arguments[i]), subject);
        }
      }
      try {
        NockFunction function = reciever.getFunction();
        return dispatch.executeCall(new NockCall(function, subject));
      }
      catch ( ExitException e ) {
        throw new NockException(e.getMessage(), this);
      }
    }
  }

  @CanResolve
  public abstract static class CheckCell extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof CellMeta;
    }
  }
}
