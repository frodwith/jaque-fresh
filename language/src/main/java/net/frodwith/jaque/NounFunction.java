package net.frodwith.jaque;

import net.frodwith.jaque.exception.ExitException;

@FunctionalInterface
public interface NounFunction {
  public Object apply(Object argument) throws ExitException;
}
