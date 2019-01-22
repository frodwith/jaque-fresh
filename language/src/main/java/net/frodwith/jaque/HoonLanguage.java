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
import net.frodwith.jaque.jet.ChildCore;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.jet.JetArm;
import net.frodwith.jaque.jet.JetHook;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.CellMeta;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.parser.CustomParser;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.HoonContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.ColdRegistration;

@TruffleLanguage.Registration(id = HoonLanguage.ID, 
                              name = "hoon",
                              mimeType = HoonLanguage.MIME_TYPE)
public final class HoonLanguage extends TruffleLanguage<HoonContext> {
  public static final String ID = "hoon";
  public static final String MIME_TYPE = "application/x-hoon";
  private static final OptionDescriptors OPTION_DESCRIPTORS;
  private static final JetTree k141 =
    new JetTree(
      new RootCore[] {
        new RootCore("k141", 141L,
          new BatteryHash[0],
          new JetArm[0],
          new JetHook[0],
          new ChildCore[0])});

  private Object kernel;

  static {
    List<OptionDescriptor> options = new ArrayList<>();
    HoonOptions.describe(options);
    OPTION_DESCRIPTORS = OptionDescriptors.create(options);
  }

  private static NockLanguage getNock() {
    return getCurrentLanguage(NockLanguage.class);
  }

  @Override
  public boolean isObjectOfLanguage(Object o) {
    return getNock().isObjectOfLanguage(o);
  }

  @Override
  protected HoonContext createContext(Env env) {
    return new HoonContext(this, env, k141);
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    if ( null == kernel ) {
      throw new Exception("hoon kernel not loaded");
    }
    else if ( !request.getArgumentNames().isEmpty() ) {
      throw new UnsupportedOperationException("can't type check arguments");
    }
    else {
      HoonContext context = getCurrentContext(HoonLanguage.class);
      Object trap = context.wish("|.\n" + request.getSource().getCharacters().toString());
      RootNode rootNode = TrapRootNode.create(new FormulaParser(this), trap);
      return Truffle.getRuntime().createCallTarget(rootNode);
    }
  }

  @Override
  public Object findMetaObject(HoonContext context, Object o) {
    return ( o instanceof Cell )
      ? context.cellMeta((Cell) o)
      : null;
  }

  private NockContext getNockContext() {
    return TruffleLanguage.getCurrentContext(NockLanguage.class);
  }

  public Object innerSlam(Object gate, Object sample) {
    return getNockContext().slamGate(gate, sample);
  }

  public CellMeta innerMeta(Cell cell) {
    return cell.getMeta(getNockContext());
  }
}
