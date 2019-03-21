package net.frodwith.jaque.dashboard;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.LocatedClass;

import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class FineStep {
  private final Cell battery;
  private final Axis toParent;
  private final LocatedClass klass;

  public FineStep(Cell battery, Axis toParent, LocatedClass klass) {
    this.battery = battery;
    this.toParent = toParent;
    this.klass = klass;
  }

  public boolean whole(Cell core) {
    return klass.known(core);
  }

  public boolean part(Cell core) {
    return Equality.equals(core.head, battery);
  }

  public Cell next(Cell core) throws ExitException {
    return Cell.require(toParent.fragment(core));
  }

  public void save(Cell core) {
    core.getMeta().setClass(klass);
  }
}
