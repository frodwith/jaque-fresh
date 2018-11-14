package net.frodwith.jaque.data;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.dashboard.FineCheck;

public final class UnregisteredClass extends UnlocatedClass {
  public UnregisteredClass(Battery battery, Assumption stable) {
    super(battery, stable);
  }

  @Override
  public final FineCheck getFine(Cell core) {
    return FineCheck.unregistered(core);
  }

  @Override
  public final boolean copyableEdit(Axis written) {
    return written.inTail();
  }
}
