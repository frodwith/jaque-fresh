package net.frodwith.jaque.dashboard;

import com.oracle.truffle.api.RootCallTarget;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.dashboard.Dashboard;

// A NockFunction represents the mathematical idea of a function picked out by
// a nock formula i.e. it represents the partial application nock(_,formula).
//
// Metaphorically, this is part of the plumbing for a nock 2 operation. In
// particular, jet handling is not modeled here, but in NockClass (cores, nock 9).
//
// These are per-dashboard, so that asts can efficiently use dashboard
// objects for dispatch (without having to check that its cached objects match
// the current dashboard being used).
public final class NockFunction implements TruffleObject {
  public final RootCallTarget callTarget;
  private final Dashboard dashboard;

  public NockFunction(RootCallTarget callTarget, Dashboard dashboard) {
    this.callTarget = callTarget;
    this.dashboard = dashboard;
  }

  public boolean ofDashboard(Dashboard dashboard) {
    return dashboard == this.dashboard;
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return NockFunctionMessageResolutionForeign.ACCESS;
  }
}
