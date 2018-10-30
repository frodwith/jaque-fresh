package net.frodwith.jaque.location;

public final class FineBuilder {
  private final List<FineStep> steps = new List<>();

  public void addStep(Cell battery, Axis toParent) {
    steps.add(new FineStep(battery, toParent));
  }

  public FineCheck finalize(Cell core) {
    return new FineCheck(steps.toArray(new FineStep[steps.size()]), core);
  }
}
