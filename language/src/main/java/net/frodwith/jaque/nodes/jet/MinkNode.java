package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.Executed;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.nodes.SoftNode;
import net.frodwith.jaque.nodes.SubjectNode;

public abstract class MinkNode extends SubjectNode {
  protected @Child @Executed SlotNode subjectSlot;
  protected @Child @Executed SlotNode formulaSlot;
  protected @Child @Executed SlotNode flySlot;
  protected @Child @Executed(with={"subjectSlot", "formulaSlot", "flySlot"})
    SoftNode softNode;

  protected MinkNode(AstContext astContext,
                     SlotNode subjectSlot,
                     SlotNode formulaSlot,
                     SlotNode flySlot) {
    this.subjectSlot = subjectSlot;
    this.formulaSlot = formulaSlot;
    this.flySlot = flySlot;
    this.softNode = new SoftNode(astContext);
  }

  @Specialization
  public Cell mink(Object subject, Object formula, Object flyGate,
                   Cell product) {
    return product;
  }

}
