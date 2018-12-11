package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.NockContext;

public abstract class FineCheck {
  public abstract boolean check(Cell core, NockContext context);
}

