package net.frodwith.jaque.location;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Dashboard;
import net.frodwith.jaque.exception.ExitException;

// One canonical persistent object per REGISTERED battery.  These objects are
// strongly held in a map, and are part of the informal execution history of an
// urbit. These objects are mutable, changing as new registration information is
// added to existing batteries.

// Importantly, unregistered batteries do not have a Registration.
// A separate class, Battery, is held in a weak cache, and even unregistered
// batteries get one.
public final class Registration {
  private final byte[] hash;
  private final Map<Object,Location> roots;
  private final ArrayList<Parents> parents;

  public Registration(byte[] hash) {
    this.hash = hash;
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
  public Location locate(Cell core, Supplier<Dashboard> dash) {
    try {
      Location root = roots.get(core.tail);
      if ( root != null ) {
        return root;
      }
      else {
        for ( Parents p : parents ) {
          Object at = p.axis.fragment(core);
          NockObject parent = Cell.require(at).getMeta().getObject(dash);
          Location child = p.map.get(parent.location);
          if ( null != child ) {
            return child;
          }
        }
        return null;
      }
    }
    catch ( ExitException e ) {
      return null;
    }
  }
}
