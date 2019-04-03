package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.hash.HashCode;
import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.parser.FormulaParser;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.runtime.NockContext;

public final class JetTree {
  public final RootCore[] roots;

  public JetTree(RootCore[] roots) {
    this.roots = roots;
  }

  public void addToMaps(Map<HashCode,Registration> hot,
                        Map<Location,AxisMap<Function<AstContext,CallTarget>>> driver) {
    for ( RootCore r : roots ) {
      r.addToMaps(null, hot, driver);
    }
  }
}
