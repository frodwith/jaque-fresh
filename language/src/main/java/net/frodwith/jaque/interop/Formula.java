package net.frodwith.jaque.interop;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.InteropLibrary;

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

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  protected static NockCallDispatchNode makeDispatch() {
    return NockCallDispatchNodeGen.create();
  }


  @ExportMessage
  public Object execute(Object[] arguments, 
      @Cached(value="makeDispatch()", allowUncached=true) NockCallDispatchNode dispatch) {
    Object subject = NockLanguage.fromArguments(arguments, 0L);
    return dispatch.executeCall(new NockCall(target, subject));
  }
}
