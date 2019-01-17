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
import net.frodwith.jaque.dashboard.ColdRegistration;

@TruffleLanguage.Registration(id = HoonLanguage.ID, 
                              name = "hoon",
                              mimeType = HoonLanguage.MIME_TYPE)
public final class HoonLanguage extends TruffleLanguage<HoonContext> {
  public static final String ID = "hoon";
  public static final String MIME_TYPE = "application/x-hoon";
  private static final OptionDescriptors OPTION_DESCRIPTORS;

  static {
    NockLanguage.installJetTree("hoon", 
      new JetTree(
        new RootCore[] {
          new RootCore("k141", 141L,
            new BatteryHash[0],
            new JetArm[0],
            new JetHook[0],
            new ChildCore[0])}));

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
    return new HoonContext(this, env);
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws ExitException {
    if ( !request.getArgumentNames().isEmpty() ) {
      // TODO: gate wrapper with typeless kick (do once below works)
      throw new UnsupportedOperationException("TODO");
    }
    HoonContext context = getCurrentContext(HoonLanguage.class);
    Object product = context.wish(request.source.getCharacters().toString());
    RootNode rootNode = RootNode.createConstantNode(product);
    return Truffle.getRuntime().createCallTarget(rootNode);
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
