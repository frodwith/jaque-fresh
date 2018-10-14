package net.frodwith.jaque.data;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;

import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = Cell.class)
public abstract class CellMessageResolution {

  @Resolve(message = "READ")
  public abstract static class CellReadNode extends Node {
    protected Object access(Cell reciever, String name) {
      if ( name == "head" ) {
        return reciever.head;
      }
      else if ( name == "tail" ) {
        return reciever.tail;
      }
      else {
        throw UnknownIdentifierException.raise(name);
      }
    }
  }

  @CanResolve
  public abstract static class CheckCell extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof Cell;
    }
  }
}
