package net.frodwith.jaque.nodes.op;

final class EditTermOpNode extends EditOpNode {
  @Override
  public Object executeEdit(Object whole, Object part) {
    return part;
  }
}
