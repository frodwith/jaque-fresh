package net.frodwith.jaque.interop;

import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;

@ExportLibrary(InteropLibrary.class)
public final class Bindings implements TruffleObject {
  private final NockContext context;
  static final TruffleObject toNoun = new Marshall();

  public Bindings(NockContext context) {
    this.context = context;
  }

  @ExportMessage
  public boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public Object getMembers(boolean includeInternal) {
    return new InteropArray("setDashboard", "toNoun");
  }

  @ExportMessage
  public boolean isMemberInvocable(String member) {
    return member.equals("setDashboard")
      || member.equals("toNoun");
  }

  @ExportMessage
  public boolean isMemberReadable(String member) {
    return member.equals("toNoun");
  }

  @ExportMessage
  public Object readMember(String member)
    throws UnsupportedMessageException,
           UnknownIdentifierException {
    if ( member.equals("toNoun") ) {
      return toNoun;
    }
    else if ( member.equals("setDashboard") ) {
      throw UnsupportedMessageException.create();
    }
    else {
      throw UnknownIdentifierException.create(member);
    }
  }

  @ExportMessage
  public Object invokeMember(String member, Object[] arguments,
    @CachedLibrary("toNoun") InteropLibrary marshalls)
    throws ArityException,
           UnsupportedTypeException,
           UnsupportedMessageException,
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
    else if ( member.equals("toNoun") ) {
      return marshalls.execute(toNoun, arguments);
    }
    else {
      throw UnknownIdentifierException.create(member);
    }
  }
}
