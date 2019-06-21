package net.frodwith.jaque.interop;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.UnsupportedTypeException;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.library.NounLibrary;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.DynamicCell;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;

@ExportLibrary(InteropLibrary.class)
public final class Formula implements TruffleObject {
  private final RootCallTarget target;

  public Formula(RootCallTarget target) {
    this.target = target;
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  private Object argumentToNoun(NounLibrary nouns, Object[] arguments, int i)
    throws UnsupportedTypeException {
    Object arg = arguments[i];
    if ( arg instanceof Wrapper ) {
      return ((Wrapper) arg).noun;
    }
    else if ( nouns.isNoun(arg) ) {
      // anything implementing the noun library is ok. HostObjects don't export
      // NounLibrary, and we would get our own internal data objects as
      // HostObjects if they were allocated by the embedder. Other exporters are
      // responsible for maintining their own context/caching invariants.  This
      // obviates the need for a NounProxy interface - just export the
      // NounLibrary and implement Proxy or TruffleObject.
      return arg;
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw UnsupportedTypeException.create(arguments, "argument " +
        Integer.toString(i) + " is invalid; only nouns can be the subject " +
        "of a nock computation.");
    }
  }

  private Object argumentsToSubject(NounLibrary nouns, Object[] arguments)
    throws UnsupportedTypeException {
    int n = arguments.length;
    if ( 0 == n ) {
      return (byte) 0;
    }
    else {
      Object head, tail = argumentToNoun(nouns, arguments, n-1);
      for ( int i = n - 2; i >= 0; --i ) {
        head = argumentToNoun(nouns, arguments, i);
        tail = new DynamicCell(head, tail);
      }
      return tail;
    }
  }

  protected static NockCallDispatchNode makeDispatch() {
    return NockCallDispatchNodeGen.create();
  }

  @ExportMessage
  public Object execute(Object[] arguments,
    @CachedLibrary(limit="3") NounLibrary nouns,
    @Cached(value="makeDispatch()", allowUncached=true)
    NockCallDispatchNode dispatch) throws UnsupportedTypeException {
    final Object subject = argumentsToSubject(nouns, arguments);
    final Object product = dispatch.executeCall(new NockCall(target, subject));
    return isPrimitive(product) ? product : new Wrapper(product);
  }

  private static boolean isPrimitive(Object o) {
    return o instanceof Integer
      || o instanceof Short
      || o instanceof Byte
      || o instanceof Boolean
      || o instanceof Long;
  }

  private static class Wrapper implements TruffleObject {
    final Object noun;

    Wrapper(Object noun) {
      this.noun = noun;
    }
  }
}
