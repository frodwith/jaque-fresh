package net.frodwith.jaque.location;

import java.util.Map;

import net.frodwith.jaque.data.Cell;

public abstract class Location {
  public final String name;
  public final Map<String,Hook> hooks;

  protected Location(String name, Map<String,Hook> hooks) {
    this.name = name;
    this.hooks = hooks;
  }

  protected abstract FineCheck buildFine(FineBuilder fine, Cell core) 
    throws ExitException;

  public final FineCheck buildFine(Cell core) throws ExitException {
    return buildFine(new FineBuilder(), core);
  }
}
