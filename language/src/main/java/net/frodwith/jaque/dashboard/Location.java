package net.frodwith.jaque.dashboard;

import com.oracle.truffle.api.TruffleLogger;

import java.util.Map;
import java.util.function.Supplier;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public abstract class Location {
  public final String name;
  public final Map<String,Hook> hooks;
  protected static final TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, Location.class);

  protected Location(String name, Map<String,Hook> hooks) {
    this.name = name;
    this.hooks = hooks;
  }

  public abstract FineCheck buildFine(Cell core, NockContext context)
    throws ExitException;

  public abstract void register(Registration registration);

  public abstract boolean copyableEdit(Axis axis);

  public void audit(FastClue clue) {
    if ( !clue.name.equals(name) ) {
      LOG.warning("name mismatch: got " + clue.name +
          ", expected " + name + ".");
    }
    if ( !clue.hooks.equals(hooks) ) {
      LOG.warning("hook mismatch: got " + clue.hooks.toString() +
          ", expected " + hooks.toString() + ".");
    }
  }
}
