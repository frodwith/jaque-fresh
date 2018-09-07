package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;

@TypeSystem({long.class, BigAtom.class, Cell.class, NockFunction.class})
public abstract class NockTypes {
}
