package net.frodwith.jaque.library;

import java.util.Iterator;

import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.GenerateLibrary.Abstract;
import com.oracle.truffle.api.library.GenerateLibrary.DefaultExport;

import net.frodwith.jaque.util.Path;
import net.frodwith.jaque.exception.ExitException;

@GenerateLibrary(assertions=NounAsserts.class)
@DefaultExport(LongExports.class)
@DefaultExport(IntegerExports.class)
public abstract class NounLibrary extends Library {
  private static final LibraryFactory<NounLibrary> 
    FACTORY = LibraryFactory.resolve(NounLibrary.class);

  public static LibraryFactory<NounLibrary> getFactory() {
    return FACTORY;
  }

  public static NounLibrary getUncached() {
    return FACTORY.getUncached();
  }

  // to avoid a chain of messages consuming stack and blowing up code
  // complexity, structural equality occurs behind a truffle boundary. atoms
  // which share a representation class should unify during the comparison step.
  public enum ShallowComparison { EQUAL, NOT_EQUAL, DEEP }
  public abstract ShallowComparison compare(Object receiver, Object other);

  // receivers which "know" things (hashes, cached call targets, etc and
  // including head/tail) should invoke various "learn" methods from this
  // library on the other noun.
  public void teach(Object receiver, Object other) {
  }

  public void learnHead(Object receiver, Object head) {
  }

  public void learnTail(Object receiver, Object tail) {
  }
  
  public void learnFormulaTarget(Object receiver, RootCallTarget target) {
    assert(isCell(receiver));
  }

  // something for learning core info, too...

  @TruffleBoundary
  private static boolean deepEquals(Object one, Object two) {
    class Frame {
      int state;
      Object a, b;

      Frame(Object a, Object b) {
        this.a = a;
        this.b = b;
        this.state = 0;
      }
    }
    NounLibrary nouns = getUncached();
    ArrayDeque<Frame> stack = new ArrayDeque<>();
    Frame top = new Frame(one, two);
    stack.push(top);

    do {
      switch ( top.state ) {
        case 0:
          switch ( nouns.compare(top.a, top.b) ) {
            case NOT_EQUAL:
              return false;

            case EQUAL:
              top = stack.pop();
              break;

            case DEEP:
              ++top.state;
              top = new Frame(nouns.tail(top.a), nouns.tail(top.b));
              stack.push(top);
              break;
          }
          break;

        case 1:
          ++top.state;
          top = new Frame(nouns.head(top.a), nouns.head(top.b));
          stack.push(top);
          break;

       case 2:
          // only nouns which had to be compared can learn from each other
          nouns.teach(top.a, top.b);
          nouns.teach(top.b, top.a);
          top = stack.pop();
          break;
      }
    } while ( !stack.isEmpty() );

    return true;
  }

  public final boolean unifyEquals(Object receiver, Object giver) {
    switch ( compare(receiver, giver) ) {
      case NOT_EQUAL:
        return false;

      case EQUAL:
        return true;

      case DEEP:
        return deepEquals(a, b);
    }
  }

  public final Path axisPath(Object receiver)
    throws ExitException {
    final long len = NounLibrary.this.bitLength(receiver);
    if ( 0 == len ) {
      throw new ExitException("0 treated as path");
    }
    return new Path() {
      public Iterator<Boolean> iterator() {
        return new Iterator<Boolean>() {
          long n = len - 1;
          public boolean hasNext() {
            return n > 0;
          }
          public Boolean next() {
            try {
              return NounLibrary.this.testBit(receiver, --n);
            }
            catch ( ExitException e ) {
              CompilerDirectives.transferToInterpreter();
              throw new AssertionError("testBit failed on atom");
            }
          }
        };
      }
    };
  }

  public final boolean axisInHead(Object receiver) throws ExitException {
    return !axisInTail(receiver);
  }

  public final boolean axisInTail(Object receiver) throws ExitException {
    return testBit(receiver, bitLength(receiver) - 2);
  }

  @Abstract(ifExported = {"isAtom", "isCell"})
  public boolean isNoun(Object receiver) {
    return false;
  }

  @Abstract(ifExported = {"fitsInInt", "fitsInLong", "asIntArray"})
  public boolean isAtom(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "isAtom")
  public long bitLength(Object receiver) throws ExitException {
    throw new ExitException("bitLength on non-atom");
  }

  @Abstract(ifExported = "isAtom")
  public boolean testBit(Object receiver, long index) throws ExitException {
    throw new ExitException("testBit on non-atom");
  }

  @Abstract(ifExported = "isAtom")
  public boolean fitsInInt(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "isAtom")
  public boolean fitsInLong(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "fitsInLong")
  public long asLong(Object receiver) {
    throw new AssertionError("treated as long without check");
  }

  @Abstract(ifExported = "isAtom")
  public boolean fitsInBoolean(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "fitsInBoolean")
  public boolean asBoolean(Object receiver) throws ExitException {
    throw new ExitException("not a boolean");
  }

  @Abstract(ifExported = "fitsInInt")
  public int asInt(Object receiver) throws ExitException {
    throw new ExitException("not an int");
  }

  @Abstract(ifExported = "isAtom")
  public int[] asIntArray(Object receiver) throws ExitException {
    // TODO: assert atoms never exit
    throw new ExitException("not an atom");
  }

  @Abstract(ifExported = {"head", "tail"})
  public boolean isCell(Object receiver) {
    return false;
  }

  @Abstract(ifExported = "isCell")
  public Object head(Object receiver) throws ExitException {
    // TODO: assert cells don't actually exit
    CompilerDirectives.transferToInterpreter();
    throw new ExitException("not a cell");
  }

  @Abstract(ifExported = "isCell")
  public Object tail(Object receiver) throws ExitException {
    // TODO: assert cells don't actually exit
    CompilerDirectives.transferToInterpreter();
    throw new ExitException("not a cell");
  }

  public Object cleanup(Object receiver) {
    // optional API to return a "better" version of yourself, so DynamicCells
    // that later learned they were cores can return a Core, etc. This isn't
    // called automatically because it's potentially expensive, but can be
    // called by users of the library at appropriate moments (say, before
    // persisting an arvo kernel)
    return receiver;
  }

  public abstract int mug(Object receiver);

}
