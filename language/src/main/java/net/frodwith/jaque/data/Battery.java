package net.frodwith.jaque.data;

import java.util.function.Supplier;

import net.frodwith.jaque.nodes.FragmentNode;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

// All battery nouns have a Battery, and some have registrations. We want to
// cache the hash for all batteries, thus this class. This class does not and
// should not store an assumption because it persists across registrations.
public final class Battery {
  public final Cell noun;
  public final Registration hot;
  public Registration cold;
  private BatteryHash hash;

  public Battery(Cell noun, BatteryHash hash,
                 Registration cold, Registration hot) {
    this.noun = noun;
    this.cold = cold;
    this.hash = hash; // may be null
    this.hot  = hot;
  }

  public BatteryHash cachedHash() {
    return hash;
  }

  public BatteryHash getHash() {
    if ( null == hash ) {
      hash = BatteryHash.hash(noun);
    }
    return hash;
  }

  public NockFunction getArm(FragmentNode fragmentNode, NockContext context)
    throws ExitException {
    return Cell.require(fragmentNode.executeFragment(noun))
           .getMeta(context).getFunction();
  }

  public NockFunction getArm(Axis axis, NockContext context)
    throws ExitException {
    return Cell.require(axis.fragment(noun))
           .getMeta(context).getFunction();
  }
}
