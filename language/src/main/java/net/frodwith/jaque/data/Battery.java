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
  private Dashboard dashboard;
  private Registration hot;
  private Registration cold;
  private BatteryHash hash;

  private Battery(Cell noun) {
    this.noun = noun;
  }

  public boolean hasHash() {
    return null != hash;
  }

  public BatteryHash getHash() {
    return hash;
  }

  public BatteryHash forceHash() {
    if ( !hasHash() ) {
      hash = BatteryHash.hash(noun);
    }
    return hash;
  }

  private void setDashboard(Dashboard dashboard) {
    if ( this.dashboard != dashboard ) {
      this.dashboard = dashboard;
      hot = null;
      cold = null;
    }
  }

  public Registration getCold(Dashboard dashboard) {
    setDashboard(dashboard);
    if ( null == cold ) {
      cold = dashboard.coldRegistration(noun);
    }
    return cold;
  }

  public Registration getHot(Dashboard dashboard) {
    setDashboard(dashboard);
    if ( null == hot ) {
      hot = dashboard.hotRegistration(forceHash());
    }
    return hot;
  }

  public Location locate(Cell core, Dashboard dashboard) {
    Location loc;
    if ( null != cold ) {
      loc = cold.locate(core, dashboard);
    }
    if ( null == loc ) {
      if ( null != hot ) {
        if ( null != (loc = hot.locate(core, dashboard)) ) {
          loc.register(dashboard.freeze(this));
          dashboard.invalidate();
        }
      }
    }
    return loc;
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


  public boolean isRegistered() {
    return (null != b.cold) || (null != b.hot);
  }

  public boolean forDashboard(Dashboard dashboard) {
  }
}
