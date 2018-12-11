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

import net.frodwith.jaque.jet.JetTree;
import net.frodwith.jaque.jet.RootCore;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.parser.CustomParser;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.ColdRegistration;

@TruffleLanguage.Registration(id = NockLanguage.ID, 
                              name = "nock",
                              mimeType = NockLanguage.MIME_TYPE)
public final class NockLanguage extends TruffleLanguage<NockContext> {
  public static final String ID = "nock";
  public static final String MIME_TYPE = "application/x-nock";

  private static final org.graalvm.polyglot.Source slamSource = 
    org.graalvm.polyglot.Source.newBuilder(ID, 
      "[9 2 10 [6 0 3] 0 2]", "<interal slam source>").buildLiteral();

  private static final Map<String,JetTree> installedJets =
    new HashMap<>();
  private static final Map<String,Map<Cell,ColdRegistration>> histories =
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
      Map<Cell,ColdRegistration> history) {
    if ( name.equals("") ) {
      throw new IllegalArgumentException("Empty history name");
    }
    else if ( histories.containsKey(name) ) {
      throw new IllegalArgumentException("multiple cold histories at " + name);
    }
    histories.put(name, history);
  }

  public static Map<Cell,ColdRegistration> findHistory(String name) {
    if ( !name.equals("") ) {
      Map<Cell,ColdRegistration> m = histories.get(name);
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
    RootCallTarget target   = NockFunction.mappedTarget(this, mapped);
    NockFunction function   = new NockFunction(target);
    RootNode rootNode       = RootNode.createConstantNode(function);
    return Truffle.getRuntime().createCallTarget(rootNode);
  }

  public static Value cons(Context context, Object head, Object tail) {
    return Value.asValue(
      new Cell(context.asValue(head).asHostObject(), 
               context.asValue(tail).asHostObject()));
  }

  public static Value slam(Context context, Object core, Object sample) {
    return Value.asValue(
        context.eval(slamSource).execute(cons(context, core, sample)));
  }
}
