package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.GenerateLibrary.Abstract;
import com.oracle.truffle.api.library.GenerateLibrary.DefaultExport;

@GenerateLibrary
@DefaultExport(LongExports.class)
public abstract class AtomLibrary extends Library {
  private static final LibraryFactory<AtomLibrary> 
    FACTORY = LibraryFactory.resolve(AtomLibrary.class);

  public static LibraryFactory<AtomLibrary> getFactory() {
    return FACTORY;
  }

  public static AtomLibrary getUncached() {
    return FACTORY.getUncached();
  }

  public boolean isAtom(Object receiver) {
    return false;
  }
  
  public boolean fitsInLong(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "fitsInLong")
  public long asLong(Object receiver) {
    CompilerDirectives.transferToInterpreter();
    throw new IllegalArgumentException();
  }
}
