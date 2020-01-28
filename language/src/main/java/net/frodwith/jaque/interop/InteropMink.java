package net.frodwith.jaque.interop;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.nodes.op.SoftOpNode;

@ExportLibrary(InteropLibrary.class)
public final class InteropMink implements TruffleObject {

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  protected static SoftOpNode makeSoft(NockContext context) { 
    return new SoftOpNode(context.getAstContext());
  }

  @ExportMessage
  public Object execute(Object[] arguments,
      @CachedContext(NockLanguage.class) NockContext context,
      @Cached(value="makeSoft(context)", allowUncached=true) SoftOpNode softNode)
      throws ArityException {

    if ( 3 != arguments.length) {
      throw ArityException.create(3, arguments.length);
    }

    for ( int i = 0; i < 10; i++) {
      System.err.println(softNode.toString());
    }

    return null;
  }

  /*
  @ExportMessage
  public Object execute(VirtualFrame frame, Object[] arguments,
      @CachedContext(NockLanguage.class) NockContext context,
      @Cached("new(context.getAstContext())") SoftNode softNode)
      throws ArityException {

    if ( 3 != arguments.length) {
      throw ArityException.create(3, arguments.length);
    }

    return null;

   return softNode.executeSoft(frame,
       arguments[0], arguments[1], arguments[2]);
  }
  */
}
