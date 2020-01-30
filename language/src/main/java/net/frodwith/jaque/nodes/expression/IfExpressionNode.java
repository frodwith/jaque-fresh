package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.nodes.NockExpressionNode;

public final class IfExpressionNode extends NockExpressionNode {
  @Child private NockExpressionNode testNode;
  @Child private NockExpressionNode yesNode;
  @Child private NockExpressionNode noNode;
  private final ConditionProfile testCondition;

  public IfExpressionNode(NockExpressionNode testNode,
                          NockExpressionNode yesNode,
                          NockExpressionNode noNode) {
    this.testNode      = testNode;
    this.yesNode       = yesNode;
    this.noNode        = noNode;
    this.testCondition = ConditionProfile.createCountingProfile();
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      long atom = testNode.executeLong(frame);
      if ( atom < 0L || atom > 1L ) {
        CompilerDirectives.transferToInterpreter();
        throw new NockException("nonloobean condition in 6", this);
      }
      else {
        if ( testCondition.profile(0L == atom) ) {
          return yesNode.executeGeneric(frame);
        }
        else {
          return noNode.executeGeneric(frame);
        }
      }
    }
    catch ( UnexpectedResultException e ) {
      throw new NockException("nonloobean condition in 6", this);
    }
  }
}
