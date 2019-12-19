package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.NockCall;

public final class NockEvalNode extends NockCallLookupNode {
  @Child private NockFunctionLookupNode lookupNode;
  @Child private SubjectNode subjectNode;

  public NockEvalNode(NockFunctionLookupNode lookupNode,
                      SubjectNode subjectNode) {
    this.lookupNode = lookupNode;
    this.subjectNode = subjectNode;
  }

  public NockCall executeLookup(VirtualFrame frame) {
    Object subject = subjectNode.executeGeneric(frame);
    CallTarget f = lookupNode.executeLookup(frame);
    return new NockCall(f, subject);
  }
}
