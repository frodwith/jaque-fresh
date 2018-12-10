package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.parser.Parseable;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;

public final class JetTree {
  public final RootCore[] roots;

  public JetTree(RootCore[] roots) {
    this.roots = roots;
  }

  public void addToMaps(NockLanguage language,
                        Map<BatteryHash,Registration> hot,
                        Map<Location, AxisMap<NockFunction>> driver) {
    for ( RootCore r : roots ) {
      r.addToMaps(language, hot, driver);
    }
  }

  public static JetTree parseOption(String option) {
    File f = new File(option);
    FileReader reader;
    Parseable input;
   
    if ( f.exists() && !f.isDirectory() ) {
      try {
        input = Parsers.newParseable(new FileReader(f));
      }
      catch ( FileNotFoundException e ) {
        throw new IllegalArgumentException(option);
      }
    }
    else {
      input = Parsers.newParseable(option);
    }
    Parser p = Parsers.newParser(Parsers.defaultConfiguration());
    List<RootCore> roots = new ArrayList<>();

    Object next = p.nextValue(input);
    while ( Parser.END_OF_INPUT != next ) {
      roots.add(RootCore.parseOption(next));
      next = p.nextValue(input);
    }

    return new JetTree(roots.toArray(new RootCore[roots.size()]));
  }
}
