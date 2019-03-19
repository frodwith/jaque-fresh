package net.frodwith.jaque.data;

import java.util.function.Supplier;
import java.util.ArrayDeque;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.dashboard.Dashboard;

import net.frodwith.jaque.nodes.*;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.exception.ExitException;

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
