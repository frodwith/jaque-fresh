package net.frodwith.jaque.data;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.oracle.truffle.api.Assumption;

import net.frodwith.jaque.runtime.Atom;

public final class NockBattery {
  private final Map<Object,Location> roots = new HashMap<>();
  private final ArrayList<Parents> parentList = new ArrayList<>();

  private static final class Parents {
    public final Object axis;
    public final Map<Location,Location> map;

    public Parents(Object axis) {
      this.axis = axis;
      this.map = new HashMap<>();
    }
  }

  public void insertRoot(Object noun, Location location) {
    roots.put(noun, location);
  }

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
}
