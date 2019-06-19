package net.frodwith.jaque.library;

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

  public int mug(Object receiver) {
    return delegate.mug(receiver);
  }
}
