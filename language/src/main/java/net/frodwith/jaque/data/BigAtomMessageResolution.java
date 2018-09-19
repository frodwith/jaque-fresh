package net.frodwith.jaque.data;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;

import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = BigAtom.class)
public class BigAtomMessageResolution {

  @Resolve(message = "READ")
  public abstract static class BigAtomReadNode extends Node {
    protected int access(BigAtom reciever, Number index) {
      return reciever.words[index.intValue()];
    }
  }

  @Resolve(message = "HAS_SIZE")
  public abstract static class BigAtomHasSizeNode extends Node {
    protected boolean access(BigAtom reciever) {
      return true;
    }
  }

  @Resolve(message = "GET_SIZE")
  public abstract static class BigAtomGetSizeNode extends Node {
    protected int access(BigAtom reciever) {
      return reciever.words.length;
    }
  }

  @CanResolve
  public abstract static class CheckBigAtom extends Node {
    protected static boolean test(TruffleObject receiver) {
      return receiver instanceof BigAtom;
    }
  }
}
