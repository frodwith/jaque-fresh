package net.frodwith.jaque.dashboard;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.exception.ExitException;

// One canonical persistent object per REGISTERED battery.  These objects are
// strongly held in a map, and are part of the informal execution history of an
// urbit. These objects are mutable, changing as new registration information is
// added to existing batteries.

// Importantly, unregistered batteries do not have a Registration.
// A separate class, Battery, is held in a weak cache, and even unregistered
// batteries get one.
public final class Registration {
  private final Map<Object,Location> roots;
  private final ArrayList<Parents> parents;

  public Registration() {
    this.roots = new HashMap<>();
    this.parents = new ArrayList<>();
  }

  private static final class Parents {
    public final Axis axis;
    public final Map<Location,Location> map;

    public Parents(Axis axis, Location parent, Location child) {
      this.axis = axis;
      this.map = new HashMap<>();
      this.map.put(parent, child);
    }
  }

  @TruffleBoundary
  public void registerRoot(Object payload, RootLocation root) {
    this.roots.put(payload, root);
  }

  @TruffleBoundary
  public void registerChild(Axis toParent, Location child, Location parent) {
    int i, len = parents.size();
L0: for ( i = 0; i < len; ++i ) {
      Parents p = parents.get(i);
      switch ( Atom.compare(p.axis.atom, toParent.atom) ) {
        case 0:
          p.map.put(parent, child);
          return;

        case 1:
          break L0;
      }
    }
    parents.add(i, new Parents(toParent, parent, child));
  }

  @TruffleBoundary
  public Optional<Location> locate(Cell core, Dashboard dashboard) {
    try {
      Location root = roots.get(core.tail);
      if ( root != null ) {
        return Optional.of(root);
      }
      else {
        for ( Parents p : parents ) {
          Cell parentCore = Cell.require(p.axis.fragment(core));
          Optional<Location> parentLocation = parentCore.getMeta()
            .getLocation(dashboard);
          NockClass parentClass = 
            parentCore.getMeta().getNockClass(parentCore, dashboard);
          if ( parentClass instanceof LocatedClass ) {
            Location found = p.map.get(((LocatedClass) parentClass).location);
            if ( null != found ) {
              return Optional.of(found);
            }
          }
        }
        return Optional.empty();
      }
    }
    catch ( ExitException e ) {
      return Optional.empty();
    }
  }

  public boolean copyableEdit(Axis axis) {
    for ( Parents p : parents ) {
      if ( axis.inside(p.axis) ) {
        return false;
      }
    }

    return true;
  }
}
