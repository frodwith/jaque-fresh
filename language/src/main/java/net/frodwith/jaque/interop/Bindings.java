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
  static final TruffleObject jam = new InteropJam();
  static final TruffleObject cue = new InteropCue();
  static final TruffleObject mug = new InteropMug();
  static final TruffleObject toBytes = new InteropToBytes();
  static final TruffleObject fromBytes = new InteropFromBytes();
  static final TruffleObject debugDump = new InteropDebugDump();

  public Bindings(NockContext context) {
    this.context = context;
  }

  @ExportMessage
  public boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public Object getMembers(boolean includeInternal) {
    return new InteropArray("installArvoJets", "setDashboard", "fromBytes", "toBytes",
                            "toNoun", "jam", "cue", "mug", "debugDump");
  }

  @ExportMessage
  public boolean isMemberInvocable(String member) {
    return member.equals("installArvoJets")
        || member.equals("setDashboard")
        || member.equals("fromBytes")
        || member.equals("toBytes")
        || member.equals("toNoun")
        || member.equals("jam")
        || member.equals("cue")
        || member.equals("mug")
        || member.equals("debugDump");
  }

  @ExportMessage
  public boolean isMemberReadable(String member) {
    return member.equals("fromBytes")
        || member.equals("toBytes")
        || member.equals("toNoun")
        || member.equals("jam")
        || member.equals("cue")
        || member.equals("mug")
        || member.equals("debugDump");
  }

  @ExportMessage
  public Object readMember(String member)
    throws UnsupportedMessageException,
           UnknownIdentifierException {
    if ( member.equals("fromBytes") ) {
      return fromBytes;
    }
    if ( member.equals("toBytes") ) {
      return toBytes;
    }
    else if ( member.equals("toNoun") ) {
      return toNoun;
    }
    else if ( member.equals("jam") ) {
      return jam;
    }
    else if ( member.equals("cue") ) {
      return cue;
    }
    else if ( member.equals("mug") ) {
      return mug;
    }
    else if ( member.equals("debugDump") ) {
      return debugDump;
    }
    else if ( member.equals("setDashboard") ) {
      throw UnsupportedMessageException.create();
    }
    else {
      throw UnknownIdentifierException.create(member);
    }
  }

  @ExportMessage
  public Object invokeMember(
      String member,
      Object[] arguments,
      @CachedLibrary("fromBytes") InteropLibrary marshallsFromBytes,
      @CachedLibrary("toBytes") InteropLibrary marshallsToBytes,
      @CachedLibrary("toNoun") InteropLibrary marshallsNoun,
      @CachedLibrary("jam") InteropLibrary marshallsJam,
      @CachedLibrary("cue") InteropLibrary marshallsCue,
      @CachedLibrary("mug") InteropLibrary marshallsMug,
      @CachedLibrary("debugDump") InteropLibrary marshallsDebugDump)
    throws ArityException,
           UnsupportedTypeException,
           UnsupportedMessageException,
           UnknownIdentifierException {
    if ( member.equals("installArvoJets") ) {
      if ( arguments.length == 0 ) {
        // It would be great if we could set the jet dashboard from the
        // launcher, but we cannot access any of the classes in net.f.j.nodes.*
        // from that context.
        context.setDashboard(ArvoJetDashboard.build());
        return true;
      }
      else {
        throw ArityException.create(0, arguments.length);
      }
    }
    else if ( member.equals("setDashboard") ) {
      if ( arguments.length == 1 ) {
        context.setDashboard(context.asDashboard(arguments[0]));
        return true;
      }
      else {
        throw ArityException.create(1, arguments.length);
      }
    }
    else if ( member.equals("fromBytes") ) {
      // This is kinda hacky, but it looks like the easiest way to get the
      // context into the specific, local, implementation class.
      Object[] toPass = new Object[]{context, arguments};
      return marshallsFromBytes.execute(fromBytes, toPass);
    }
    else if ( member.equals("toBytes") ) {
      return marshallsToBytes.execute(toBytes, arguments);
    }
    else if ( member.equals("toNoun") ) {
      return marshallsNoun.execute(toNoun, arguments);
    }
    else if ( member.equals("jam") ) {
      return marshallsJam.execute(jam, arguments);
    }
    else if ( member.equals("cue") ) {
      return marshallsCue.execute(cue, arguments);
    }
    else if ( member.equals("mug") ) {
      return marshallsMug.execute(mug, arguments);
    }
    else if ( member.equals("debugDump") ) {
      return marshallsDebugDump.execute(debugDump, arguments);
    }
    else {
      throw UnknownIdentifierException.create(member);
    }
  }
}
