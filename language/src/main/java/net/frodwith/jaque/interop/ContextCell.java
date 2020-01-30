package net.frodwith.jaque.interop;

import java.util.Optional;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.data.NockCall;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.parser.SimpleAtomParser;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.nodes.dispatch.HeadDispatchNode;

@ExportLibrary(InteropLibrary.class)
public final class ContextCell implements TruffleObject {
  private final NockContext context;
  private final Cell cell;
  private final CellMeta meta;
  private Optional<Boolean> isExecutable;

  public ContextCell(NockContext context, Cell cell) {
    this.context = context;
    this.cell = cell;
    this.meta = cell.getMeta();
    this.isExecutable = Optional.empty();
  }

  public NockCall getCall(Axis axis) throws ExitException {
    CallTarget arm;
    if ( axis.inHead() ) {
      arm = meta.getNockClass(cell, context.getDashboard())
        .getArm(cell, axis, context.getAstContext());
    }
    else {
      Cell formula = Cell.require(axis.fragment(cell));
      arm = formula.getMeta()
        .getFunction(formula, context.getAstContext()).callTarget;
    }
    return new NockCall(arm, cell);
  }

  public ContextCell edit(Axis axis, Object sample) throws ExitException {
    Object subject = axis.edit(cell, sample);
    return new ContextCell(context, Cell.require(subject));
  }
  
  @ExportMessage
  public boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public Object getMembers(boolean includeInternal) {
    return new InteropArray("isCore", "isFormula");
  }

  @ExportMessage
  public Object readMember(String memberName) throws UnknownIdentifierException {
    if ( memberName.equals("isCore") ) {
      return meta.hasClass(context.getDashboard());
    }
    else if ( memberName.equals("isFormula") ) {
      return meta.hasFunction(context.getAstContext());
    }
    else {
      throw UnknownIdentifierException.create(memberName);
    }
  }

  @ExportMessage
  public boolean isExecutable() {
    if ( isExecutable.isPresent() ) {
      return isExecutable.get();
    }
    else {
      boolean success;
      try {
        meta.getFunction(cell, context.getAstContext());
        success = true;
      }
      catch ( ExitException e ) {
        success = false;
      }
      isExecutable = Optional.of(success);
      return success;
    }
  }

  private CallTarget
    getArm(String member) 
      throws UnknownIdentifierException,
             UnsupportedMessageException {
    Axis axis;
    try {
      axis = Axis.require(SimpleAtomParser.parse(member));
    }
    catch ( IllegalArgumentException | ExitException e ) {
      throw UnknownIdentifierException.create(member);
    }
    try {
      return meta.getNockClass(cell, context.getDashboard())
        .getArm(cell, axis, context.getAstContext());
    }
    catch (ExitException e) {
      throw UnsupportedMessageException.create();
    }
  }

  @ExportMessage
  public boolean isMemberReadable(String member) {
    return member.equals("isFormula")
      || member.equals("isCore");
  }

  @ExportMessage
  public boolean isMemberInvocable(String member) {
    try {
      CallTarget target = getArm(member);
      return true;
    }
    catch ( UnsupportedMessageException | UnknownIdentifierException e ) {
      return false;
    }
  }

  @ExportMessage
  public Object invokeMember(String member, Object[] arguments,
    @CachedLanguage NockLanguage language,
    @Cached(value="create()", allowUncached=true)
    HeadDispatchNode dispatchNode)
      throws UnsupportedMessageException,
             UnsupportedTypeException,
             UnknownIdentifierException {

    Object subject;
    if ( 0 == arguments.length ) {
      subject = cell;
    }
    else {
      Object sample = language.argumentsToSubject(arguments);
      try {
        subject = Axis.SAMPLE.edit(cell, sample);
      }
      catch ( ExitException e ) {
        throw UnsupportedMessageException.create();
      }
    }

    return dispatchNode.executeDispatch(getArm(member), subject);
  }

  @ExportMessage
  public Object execute(Object[] arguments,
    @CachedLanguage NockLanguage language,
    @Cached(value="create()", allowUncached=true)
    HeadDispatchNode dispatchNode)
      throws ArityException,
             UnsupportedTypeException,
             UnsupportedMessageException {
    NockFunction f;
    try {
      f = meta.getFunction(cell, context.getAstContext());
    }
    catch ( ExitException e ) {
      throw UnsupportedMessageException.create();
    }
    Object subject = language.argumentsToSubject(arguments);
    return dispatchNode.executeDispatch(f.callTarget, subject);
  }
}
