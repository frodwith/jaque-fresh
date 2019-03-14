package net.frodwith.jaque;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionDescriptors;

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
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.parser.CustomParser;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

@TruffleLanguage.Registration(id = NockLanguage.ID, 
                              name = "nock",
                              mimeType = NockLanguage.MIME_TYPE)
public final class NockLanguage extends TruffleLanguage<NockContext> {
  public static final String ID = "nock";
  public static final String MIME_TYPE = "application/x-nock";

  private final FormulaParser formulaParser = new FormulaParser(this);

  private static final Map<String,JetTree> installedJets =
    new HashMap<>();
  private static final Map<String,Map<Cell,net.frodwith.jaque.dashboard.Registration>> histories =
    new HashMap<>();

  public static final FrameDescriptor DESCRIPTOR;
  protected static final FrameSlot SUBJECT_SLOT;
  private static final OptionDescriptors OPTION_DESCRIPTORS;

  static {
    DESCRIPTOR = new FrameDescriptor();
    SUBJECT_SLOT = DESCRIPTOR.addFrameSlot("subject");
    SUBJECT_SLOT.setKind(FrameSlotKind.Object);
    List<OptionDescriptor> options = new ArrayList<>();
    NockOptions.describe(options);
    OPTION_DESCRIPTORS = OptionDescriptors.create(options);
  }

  public static void installJetTree(String name, JetTree tree) {
    if ( name.equals("") ) {
      throw new IllegalArgumentException("Empty jet tree name");
    }
    else if ( installedJets.containsKey(name) ) {
      throw new IllegalArgumentException("multiple jet trees at " + name);
    }
    else {
      installedJets.put(name, tree);
    }
  }

  public static JetTree getJetTree(String name) {
    if ( !name.equals("") ) {
      JetTree t = installedJets.get(name);
      if ( null != t ) {
        return t;
      }
    }
    return new JetTree(new RootCore[0]);
  }


  public static void registerHistory(String name, 
      Map<Cell,net.frodwith.jaque.dashboard.Registration> history) {
    if ( name.equals("") ) {
      throw new IllegalArgumentException("Empty history name");
    }
    else if ( histories.containsKey(name) ) {
      throw new IllegalArgumentException("multiple cold histories at " + name);
    }
    histories.put(name, history);
  }

  public static Map<Cell,net.frodwith.jaque.dashboard.Registration> findHistory(String name) {
    if ( !name.equals("") ) {
      Map<Cell,net.frodwith.jaque.dashboard.Registration> m = histories.get(name);
      // XX: need to copy this so it's not shared between contexts
      if ( null != m ) {
        return m;
      }
    }
    return new HashMap<>();
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
      || o instanceof CellMeta
      || o instanceof Cell
      || o instanceof BigAtom
      || o instanceof Long;
  }

  @Override
  protected NockContext createContext(Env env) {
    return new NockContext(this, env);
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws ExitException {
    Source source = request.getSource();
    if ( !request.getArgumentNames().isEmpty() ) {
      throw new UnsupportedOperationException("nock has no named values");
    }
    SourceSection whole     = source.createSection(0, source.getLength());
    SourceMappedNoun mapped = CustomParser.parse(whole);
    RootCallTarget target   = formulaParser.mappedTarget(mapped);
    NockFunction function   = new NockFunction(target);
    RootNode rootNode       = RootNode.createConstantNode(function);
    return Truffle.getRuntime().createCallTarget(rootNode);
  }

  @Override
  public Object findMetaObject(NockContext context, Object o) {
    if ( o instanceof Cell ) {
      return ((Cell) o).getFIXMEMetaObject(context);
    }
    else {
      return null;
    }
  }

  @TruffleBoundary
  private static long fromForeignNumber(Object a) {
    return ((Number) a).longValue();
  }

  public static Object fromForeignValue(Object a) {
    if ( a instanceof Long || a instanceof BigAtom || a instanceof Cell ) {
      return a;
    }
    else if ( a instanceof Character ) {
      return (long) ((Character) a).charValue();
    }
    else if ( a instanceof Number ) {
      return fromForeignNumber(a);
    }
    else {
      CompilerDirectives.transferToInterpreter();
      throw new IllegalArgumentException(a + " is not a noun");
    }
  }

  public static Object fromArguments(Object[] arguments) {
    Object product = fromForeignValue(arguments[arguments.length-1]);
    for ( int i = arguments.length-2; i >= 0; --i ) {
      product = new Cell(fromForeignValue(arguments[i]), product);
    }
    return product;
  }

  public static Object fromArguments(Object[] arguments, Object nullValue) {
    return (null == arguments || 0 == arguments.length)
      ? nullValue
      : fromArguments(arguments);
  }
}
