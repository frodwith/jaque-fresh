package net.frodwith.jaque.exception;

import com.oracle.truffle.api.nodes.SlowPathException;

// Jet nodes, when they should !! in hoon, will finally throw a Bail,
// which is a TruffleException and contains things like the offending node.
//
// Our convention is to to have a java-callable version of internal library
// functions, which if they would Bail, instead throw Fail. Then in the jet
// node, these Fails are caught (in a slow path, not compiled by graal) and a
// Bail is constructed with the node for the jet. In this way, we never
// compile hoon error paths - they always deoptimize and keep our control flow
// analysis nice and clean.
//
// As a consequence, many internal library functions throw Fail, which is a
// checked exception. That can get kind of ugly. I'm sorry, that's just the
// way it has to be.
//
public class Fail extends SlowPathException {
  public Fail(String message) {
    super(message);
  }

  public Fail(String message, Throwable cause) {
    super(message, cause);
  }
}
