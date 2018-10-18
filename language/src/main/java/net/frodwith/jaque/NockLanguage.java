package net.frodwith.jaque;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;

import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.parser.CustomParser;

@TruffleLanguage.Registration(id = NockLanguage.ID, 
                              name = "nock",
                              mimeType = NockLanguage.MIME_TYPE)
public final class NockLanguage extends TruffleLanguage<NockContext> {
  public static final String ID = "nock";
  public static final String MIME_TYPE = "application/x-nock";

  public static final FrameDescriptor DESCRIPTOR;
  protected static final FrameSlot SUBJECT_SLOT;

  static {
    DESCRIPTOR = new FrameDescriptor();
    SUBJECT_SLOT = DESCRIPTOR.addFrameSlot("subject");
    SUBJECT_SLOT.setKind(FrameSlotKind.Object);
  }

  /* Nock's only local variable is the subject. */
  public static Object getSubject(VirtualFrame frame) {
    try {
      return frame.getObject(SUBJECT_SLOT);
    }
    catch (FrameSlotTypeException e) {
      throw new RuntimeException();
    }
  }

  public static void setSubject(VirtualFrame frame, Object subject) {
    frame.setObject(SUBJECT_SLOT, subject);
  }

  @Override
  public boolean isObjectOfLanguage(Object o) {
    return o instanceof NockFunction
      || o instanceof Cell
      || o instanceof BigAtom
      || o instanceof Long;
  }

  @Override
  protected NockContext createContext(Env env) {
    return new NockContext(this, env);
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws ExitException {
    Source source = request.getSource();
    if ( !request.getArgumentNames().isEmpty() ) {
      throw new UnsupportedOperationException("nock has no named values");
    }
    SourceSection whole     = source.createSection(0, source.getLength());
    SourceMappedNoun mapped = CustomParser.parse(whole);
    RootCallTarget target   = NockFunction.mappedTarget(this, mapped);
    NockFunction function   = new NockFunction(target);
    RootNode rootNode       = RootNode.createConstantNode(function);
    return Truffle.getRuntime().createCallTarget(rootNode);
  }
}
