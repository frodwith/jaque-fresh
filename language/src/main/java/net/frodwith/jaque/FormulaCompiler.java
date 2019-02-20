package net.frodwith.jaque;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.exception.ExitException;

public interface FormulaCompiler {
  public NockFunction compile(Cell formula) throws ExitException;
}
