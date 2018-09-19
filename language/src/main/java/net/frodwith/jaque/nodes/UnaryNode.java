package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild(value = "value", type = NockExpressionNode.class)
public abstract class UnaryNode extends NockExpressionNode {
}
