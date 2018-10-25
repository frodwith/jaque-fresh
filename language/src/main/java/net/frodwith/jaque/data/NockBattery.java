package net.frodwith.jaque.data;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.runtime.Atom;

public final class NockBattery {
  private final Map<Object,Location> roots = new HashMap<>();
  private final ArrayList<Parents> parentList = new ArrayList<>();
  private final Cell noun;

  private static final class Parents {
    public final Axis axis;
    public final Map<Location,Location> map;

    public Parents(Axis axis) {
      this.axis = axis;
      this.map = new HashMap<>();
    }
  }

  @TruffleBoundary
  public void insertRoot(Object noun, Location location) {
    roots.put(noun, location);
  }

  @TruffleBoundary
  public void insertChild(Object axisToParent, Location child, Location parent) {
    int i, len = parentList.size();
    for ( i = 0; i < len; ++i ) {
      Parents p = parentList.get(i);
      switch ( Atom.compare(axisToParent, p.axis) ) {
        case 0:
          p.map.put(parent, child);
          return;

        case -1:
          p = new Parents(axisToParent);
          p.map.put(parent, child);
          parentList.add(i, p);
          return;
      }
    }
  }

  @TruffleBoundary
  public Location locate(Cell core, Dashboard dashboard) {
    try {
      Location root = roots.get(core.tail);
      if ( root != null ) {
        return root;
      }
      else {
        for ( Parents p : parentList ) {
          Object at = p.axis.fragment(core);
          NockObject parent = Cell.require(at).getMeta().getObject(dashboard);
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
