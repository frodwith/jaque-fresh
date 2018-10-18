package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.TypeSystemReference;

@ReportPolymorphism
@TypeSystemReference(NockTypes.class)
@NodeInfo(language = "nock")
public abstract class NockNode extends Node {
  // this space left intentionally blank
}
