package net.frodwith.jaque;

import net.frodwith.jaque.exception.ExitException;

@FunctionalInterface
public interface NounPredicate {
  public boolean test(Object noun) throws ExitException;
}
