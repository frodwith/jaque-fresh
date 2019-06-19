package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.GenerateLibrary.Abstract;
import com.oracle.truffle.api.library.GenerateLibrary.DefaultExport;

import net.frodwith.jaque.library.CellLibrary.Asserts;

@GenerateLibrary(assertions = Asserts.class)
public abstract class CellLibrary extends Library {
  private static final LibraryFactory<AtomLibrary> 
    FACTORY = LibraryFactory.resolve(AtomLibrary.class);

  public static LibraryFactory<AtomLibrary> getFactory() {
    return FACTORY;
  }

  public static AtomLibrary getUncached() {
    return FACTORY.getUncached();
  }

  public boolean isCell(Object receiver) {
    return false;
  }

  static class Asserts extends CellLibrary {
    @Child private CellLibrary delegate;

    Asserts(CellLibrary delegate) {
      this.delegate = delegate;
    }

    public boolean accepts(Object receiver) {
      return delegate.accepts(receiver);
    }

//    public boolean isCell(Object receiver, @CachedLibrary("receiver")
//        AtomLibrary receivers) {
    @Override
    public boolean isCell(Object receiver) {
      boolean wasCell = delegate.isCell(receiver);
      assert wasCell != AtomLibrary.getUncached().isAtom(receiver)
        : "cells are never atoms";
      return wasCell;
    }
  }
}
