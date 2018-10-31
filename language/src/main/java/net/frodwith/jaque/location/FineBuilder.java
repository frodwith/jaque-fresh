package net.frodwith.jaque.location;

import java.util.List;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Axis;

public final class FineBuilder {
  private final List<FineStep> steps = new List<>();

  public void addStep(Cell battery, Axis toParent) {
    steps.add(new FineStep(battery, toParent));
  }

  public FineCheck finalize(Cell core) {
    return new FineCheck(steps.toArray(new FineStep[steps.size()]), core);
  }
}
