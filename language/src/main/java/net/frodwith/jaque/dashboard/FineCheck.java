package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;

public abstract class FineCheck {
  public abstract boolean check(Cell core, Supplier<Dashboard> supply);
}

