package net.frodwith.jaque.data;

// These are cords that fit into a 32 bit int, for use in switch statements.
// They aren't longs, so they aren't nouns. Be careful.

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.exception.ExitException;

public final class Motes {
  public static final int
    FAST = 0x74736166,
    CORE = 0x65726f63,
    MEMO = 0x6f6d656d,
    ROSE = 0x65736f72,
    PALM = 0x6d6c6170,
    LEAF = 0x6661656c,
    SPOT = 0x746f7073,
    SLOG = 0x676f6c73;
}
