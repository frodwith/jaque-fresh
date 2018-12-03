package net.frodwith.jaque.data;

import java.util.function.Supplier;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockFunctionRegistry;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.ColdRegistration;

// All battery nouns have a Battery, and some have registrations. We want to
// cache the hash for all batteries, thus this class. This class does not and
// should not store an assumption because it persists across registrations.
public final class Battery {
  public final Cell noun;
  public final Registration hot;
  public final BatteryHash hash;
  public ColdRegistration cold;

  public Battery(Cell noun, BatteryHash hash,
                 ColdRegistration cold, Registration hot) {
    this.noun = noun;
    this.cold = cold;
    this.hash = hash;
    this.hot  = hot;
  }

  public NockFunction
    getArm(FragmentNode fragmentNode,
           Supplier<NockFunctionRegistry> supply)
      throws ExitException {
    return Cell.require(fragmentNode.executeFragment(noun)).getMeta().getFunction(supply);
  }
}
