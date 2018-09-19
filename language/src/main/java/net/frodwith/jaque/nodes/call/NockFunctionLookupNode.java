package net.frodwith.jaque.nodes.call;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.nodes.NockTypes;
import net.frodwith.jaque.nodes.NockExpressionNode;

@ReportPolymorphism
@TypeSystemReference(NockTypes.class)
@NodeChild(value="cellNode", type=NockExpressionNode.class)
@NodeField(name="contextReference", type=ContextReference.class)
public abstract class NockFunctionLookupNode extends Node {
  public abstract NockFunction executeLookup(VirtualFrame frame);
  protected abstract ContextReference<NockContext> getContextReference();

  @Specialization
  protected NockFunction doCell(Cell formula) {
    try {
      return getContextReference().get().lookupFunction(formula);
    }
    catch (Fail e) {
      throw new Bail("bad formula", this);
    }
  }
  
  @Fallback
  protected NockFunction dopAtom(Object atom) {
    throw new Bail("atom not formula", this);
  }
}
