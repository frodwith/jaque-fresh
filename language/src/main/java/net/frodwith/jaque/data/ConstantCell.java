package net.frodwith.jaque.data;

// what if the parser only worked on ConstantCells? bit of a bootstrapping
// conundrum, but would save the context.intern call... and would probably be
// faster.

@ExportLibrary(NounLibrary.class)
public final class ConstantCell {
  public final Object head, tail;
  public final int mug;
  public final Lazy<Optional<RootCallTarget>> formula;
  public final Lazy<Optional<Battery>> battery;
  public final Lazy<Optional<Core>> core;

  public ConstantCell(Object head, Object tail, int mug,
    Lazy<Optional<RootCallTarget>> formula,
    Lazy<Optional<Battery>> battery,
    Lazy<Optional<Core>> core) {
    this.head = head;
    this.tail = tail;
    this.mug = mug;
    this.formula = formula;
    this.battery = battery;
    this.core = core;
  }

  @ExportMessage boolean isNoun() {
    return true;
  }

  @ExportMessage boolean isCell() {
    return true;
  }

  @ExportMessage Object head() {
    return head;
  }

  @ExportMessage Object tail() {
    return tail;
  }

  @ExportMessage int mug() {
    return mug;
  }

  @ExportMessage int cachedMug() {
    return mug;
  }

  @ExportMessage NounLibrary.ShallowComparison compare(Object other,
    @CachedLibrary("other") NounLibrary others) {
    return ( other == this )
      ? NounLibrary.ShallowComparison.EQUAL
      : ( null == others.knownConstantCell(other) )
      ? NounLibrary.ShallowComparison.DEEP
      : NounLibrary.ShallowComparison.NOT_EQUAL;
  }

  private static <T> T lazyGet(Lazy<Optional<T>> field, String typeName)
    throws ExitException {
    Optional<T> valid = field.get();
    if ( valid.isPresent() ) {
      return valid.get();
    }
    else {
      throw new ExitException("invalid " + typeName);
    }
  }

  public RootCallTarget getFormula() throws ExitException {
    return lazyGet(formula, "formula");
  }

  public Battery getBattery() throws ExitException {
    return lazyGet(battery, "battery");
  }

  @ExportMessage
  public Core getCore() throws ExitException {
    return lazyGet(core, "core");
  }

  @ExportMessage
  public ConstantCell knownConstantCell(Object receiver) {
    return this;
  }

  @ExportMessage
  public void teach(Object other, @CachedLibrary("other") NounLibrary others) {
    others.learnConstantCell(this);
  }
}
