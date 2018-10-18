package net.frodwith.jaque.runtime;

import java.util.Map;
import java.util.HashMap;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.NockLanguage;

import net.frodwith.jaque.exception.ExitException;

public final class NockFunctionRegistry {
  private final NockLanguage language;
  private Map<Cell,NockFunction> map = new HashMap<>();

  public NockFunctionRegistry(NockLanguage language) {
    this.language = language;
  }

  public NockFunction lookup(Cell formula) throws ExitException {
    NockFunction f = map.get(formula);
    if ( null == f ) {
      f = new NockFunction(NockFunction.cellTarget(language, formula));
      map.put(formula, f);
    }
    return f;
  }
}
