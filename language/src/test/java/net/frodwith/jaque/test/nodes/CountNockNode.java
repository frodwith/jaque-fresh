package net.frodwith.jaque.test.nodes;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.NockCall;

import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.NockContext;

import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.NockControlFlowException;

public final class CountNockNode extends SubjectNode {
  private final ContextReference<NockContext> contextReference;
  private final Axis armAxis;
  private final String countName;
  private static final Map<String,Integer> counts = new HashMap<>();

  public CountNockNode(ContextReference<NockContext> contextReference,
                       Axis armAxis,
                       String countName) {
    this.contextReference = contextReference;
    this.armAxis = armAxis;
    this.countName = countName;
  }

  private void bump() {
    counts.put(countName, count(countName) + 1);
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      Object subject = NockLanguage.getSubject(frame);
      Cell formula = Cell.require(armAxis.fragment(subject));
      NockFunction fn = formula.getMeta().getFunction(contextReference.get());
      NockCall call = new NockCall(fn, subject);
      bump();
      throw new NockControlFlowException(call);
    }
    catch ( ExitException e ) {
      throw new NockException("count bail", this);
    }
  }

  public static int count(String name) {
    return counts.containsKey(name) ? counts.get(name) : 0;
  }
}
