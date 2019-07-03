package net.frodwith.jaque.library;

import net.frodwith.jaque.exception.ExitException;

final class NounAsserts extends NounLibrary {
  @Child private NounLibrary delegate;

  NounAsserts(NounLibrary delegate) {
    this.delegate = delegate;
  }

  public boolean accepts(Object receiver) {
    return delegate.accepts(receiver);
  }

  public boolean isNoun(Object receiver) {
    return delegate.isNoun(receiver);
  }

  public boolean isAtom(Object receiver) {
    return delegate.isAtom(receiver);
  }

  public long bitLength(Object receiver) throws ExitException {
    return delegate.bitLength(receiver);
  }

  public boolean testBit(Object receiver, long index) throws ExitException {
    return delegate.testBit(receiver, index);
  }

  public boolean fitsInInt(Object receiver) {
    return delegate.fitsInInt(receiver);
  }

  public boolean fitsInLong(Object receiver) {
    return delegate.fitsInLong(receiver);
  }

  public long asLong(Object receiver) {
    return delegate.asLong(receiver);
  }

  public int asInt(Object receiver) throws ExitException {
    return delegate.asInt(receiver);
  }

  public int[] asIntArray(Object receiver) throws ExitException {
    return delegate.asIntArray(receiver);
  }

  public boolean isCell(Object receiver) {
    return delegate.isCell(receiver);
  }

  public Object head(Object receiver) throws ExitException {
    return delegate.head(receiver);
  }
  public Object tail(Object receiver) throws ExitException {
    return delegate.tail(receiver);
  }

  public Object cleanup(Object receiver) {
    return delegate.cleanup(receiver);
  }

  public int mug(Object receiver) {
    return delegate.mug(receiver);
  }
}
