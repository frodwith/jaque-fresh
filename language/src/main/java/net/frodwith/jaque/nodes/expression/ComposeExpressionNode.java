package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import net.frodwith.jaque.NockLanguage;

public final class ComposeExpressionNode extends NockExpressionNode {
  @Child private NockExpressionNode headNode;
  @Child private NockExpressionNode tailNode;

  public ComposeExpressionNode(NockExpressionNode headNode,
                               NockExpressionNode tailNode) {
    this.headNode = headNode;
    this.tailNode = tailNode;
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object oldSubject  = NockLanguage.getSubject(frame);
    Object headProduct = headNode.executeGeneric(frame);
    NockLanguage.setSubject(frame, headProduct);
    Object tailProduct = tailNode.executeGeneric(frame);
    NockLanguage.setSubject(frame, oldSubject);
    return tailProduct;
  }
}
