package net.frodwith.jaque.interop;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.UnsupportedTypeException;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.nodes.NockCallDispatchNode;
import net.frodwith.jaque.nodes.NockCallDispatchNodeGen;

@ExportLibrary(InteropLibrary.class)
public final class Formula implements TruffleObject {
  private final RootCallTarget target;

  public Formula(RootCallTarget target) {
    this.target = target;
  }

  protected static NockCallDispatchNode makeDispatch() {
    return NockCallDispatchNodeGen.create();
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments,
    @CachedLanguage NockLanguage language,
    @Cached(value="makeDispatch()", allowUncached=true)
    NockCallDispatchNode dispatch) throws UnsupportedTypeException {
    Object subject = language.argumentsToSubject(arguments);
    return dispatch.executeCall(new NockCall(target, subject));
  }
}