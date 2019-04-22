package net.frodwith.jaque.runtime;

import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;

import net.frodwith.jaque.dashboard.Dashboard;

@ExportLibrary(InteropLibrary.class)
public final class NockInterop implements TruffleObject {
  private final NockContext context;

  public NockInterop(NockContext context) {
    this.context = context;
  }

  @ExportMessage
  public boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public Object getMembers(boolean includeInternal) {
    return new InteropArray("setDashboard");
  }

  @ExportMessage
  public boolean isMemberInvocable(String member) {
    return member.equals("setDashboard");
  }

  @ExportMessage
  public Object invokeMember(String member, Object... arguments)
    throws ArityException,
           UnsupportedTypeException,
           UnknownIdentifierException {
    if ( member.equals("setDashboard") ) {
      if ( arguments.length == 1 ) {
        context.setDashboard(context.asDashboard(arguments[0]));
        return true;
      }
      else {
        throw ArityException.create(1, arguments.length);
      }
    }
    else {
      throw UnknownIdentifierException.create(member);
    }
  }
}
