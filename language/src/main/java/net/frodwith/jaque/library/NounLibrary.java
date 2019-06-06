package net.frodwith.jaque.library;

import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.GenerateLibrary.DefaultExport;

@GenerateLibrary
@DefaultExport(LongExports.class)
public abstract class NounLibrary extends Library {
  private static final LibraryFactory<NounLibrary> 
    FACTORY = LibraryFactory.resolve(NounLibrary.class);

  public static LibraryFactory<NounLibrary> getFactory() {
    return FACTORY;
  }

  public static NounLibrary getUncached() {
    return FACTORY.getUncached();
  }

  public boolean isNoun(Object receiver) {
    return false;
  }

  public final boolean isCell(Object receiver) {
    return false;
  }

  public final boolean isAtom(Object receiver) {
    return isNoun(receiver) && !isCell(receiver);
  }

  public abstract int mug(Object receiver);
}
