package net.frodwith.jaque.data;

// a constant noun is a either a constant cell or a constant atom
// a constant cell a ConstantCell with constant nouns in its head/tail
// a constant atom is either a long or a ConstantAtom

// two constant nouns can be compared for equality by reference in a context,
// because they are interned (and hence deduplicated).
@ExportLibrary(NounLibrary.class)
public final class ConstantCell {
  final static HashFunction hashFunction = Hashing.sha256();

  final Object head, tail;
  final int mug;
  final Lazy<HashCode> strongHash;
  final Lazy<Optional<Formula>> formula;
  final Lazy<Optional<Core>> core;

  public ConstantCell(NockLanguage language, Object head, Object tail) {
    this.head = head;
    this.tail = tail;
    this.mug = Mug.both(subMug(head), subMug(tail));
    this.strongHash = buildHashCode();
    this.formula = buildFormula();
    this.core = buildCore();
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

  public RootCallTarget getFormulaTarget() throws ExitException {
    return lazyGet(formula, "formula").
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

  private NodeBuilder parse() throws ExitException {
    if ( head instanceof ConstantCell ) {
      return parseCons();
    }
    else if ( !(head instanceof ConstantAtom) ) {
      switch ( (int) head ) {
        case 0:
          return parseSlot();
        case 1:
          return parseQuot();
        case 2:
          return parseNock();
        case 3:
          return parseDeep();
        case 4:
          return parseBump();
        case 5:
          return parseSame();
        case 6:
          return parseCond();
        case 7:
          return parseComp();
        case 8:
          return parsePush();
        case 9:
          return parsePull();
        case 10:
          return parseHint();
        case 11:
          return parseEdit();
        case 12:
          return parseWish();
      }
    }
    throw new ExitException("invalid formula");
  }

  static final class NBArgs {
    // direct context references are used since there is no node sharing
    final NockContext context;
    final Lazy<SourceMappedNoun> mappedFormula;
    final AxisBuilder axis;
    final boolean product;

    NBArgs(NockContext context,
           Lazy<SourceMappedNoun> mappedFormula, 
           AxisBuilder axis, 
           boolean product) {
      this.context = context;
      this.mappedFormula = formula;
      this.axis = axis;
      this.product = product;
    }

    NBArgs axis(AxisBuilder axis, boolean product) {
      return new NBArgs(context, mappedFormula, axis, product);
    }

    private NBArgs head(boolean product) {
      return axis(axis.head(), tail);
    }

    private NBArgs tail(boolean product) {
      return axis(axis.tail(), tail);
    }

    NBArgs head() {
      return head(product);
    }

    NBArgs tail() {
      return tail(product);
    }

    NBArgs headP() {
      return head(true);
    }

    NBArgs tailP() {
      return tail(true);
    }

    NBArgs product() {
      return product ? this : new NBArgs(context, mappedFormula, axis, true);
    }
  }

  @FunctionalInterface
  static interface NodeBuilder {
    NockExpressionNode build(NBArgs c);
  }

  private static Object atom(Object o) throws ExitException {
    if ( o instanceof ConstantCell ) {
      throw new ExitException("atom required");
    }
    else {
      return o;
    }
  }

  private static ConstantCell cell(Object o) throws ExitException {
    if ( o instanceof ConstantCell ) {
      return o;
    }
    else {
      throw new ExitException("cell required");
    }
  }

  private static NockExpressionNode axe(NBArgs args, NockExpressionNode node) {
    final Iterable<Boolean> path = NounLibrary.getUncached()
      .axisPath(args.axis.write());
    node.setSourceSection(new Lazy(() -> args.formulaMap.get().lookupAxis(path)));
    return node;
  }

  static final class Formula {
    final NodeBuilder builder;
    final Lazy<Battery> battery;
    final Lazy<RootCallTarget> target; // the "bare" call target (no source map)

    Formula(NodeBuilder builder,
            Lazy<RootCallTarget> target,
            Lazy<Battery> battery) {
      this.builder = builder;
      this.target = target;
      this.battery = battery;
    }

    NockExpressionNode node(NBArgs a) {
      return builder.build(a);
    }
  }

  // making it possible to manufacture a call target with a genuine source map
  public Optional<RootCallTarget> mappedTarget(NockContext context, 
    SourceSection section, AxisMap<IndexLength> locations) {
    if ( !formula.isPresent() ) {
      return Optional.empty();
    }
    else {
      SourceMappedNoun mapped = new SourceMappedNoun(section, locations, this);
      NockRootNode rootNode = new NockRootNode(context.getLanguage(), formula.get()
        .node(new NBArgs(context, mapped, AxisBuilder.EMPTY, true)));
      RootCallTarget target = Truffle.getRuntime.createCallTarget(rootNode);
      return Optional.of(target);
    }
  }

  // the synthetic source map
  private Lazy<SourceMappedNoun> buildSourceMap() {
    return new Lazy(() -> {
      StringWriter out = new StringWriter();
      AxisMap<IndexLength> axisMap;
      try {
        axisMap = MappedNounPrinter.print(out, this);
      }
      catch (IOException e) {
        throw new AssertionError("StringWriter threw IOException");
      }
      String text = out.toString();
      Source source = Source.newBuilder(text)
        .language("nock")
        .name(Integer.toString(hashCode(), 32))
        .internal()
        .build();
      SourceSection whole = source.createSection(0, text.length());
      return new SourceMappedNoun(whole, axisMap, this);
    });
  }

  private Lazy<Optional<Formula>> buildFormula(NockContext context) {
    return new Lazy(() -> {
      try {
        NodeBuilder builder = parse();
        NBArgs rootArgs = new NBArgs(context,
          buildSourceMap(), AxisBuilder.EMPTY, true);
        Lazy<RootCallTarget> target = new Lazy(() -> 
          Truffle.getRuntime().createCallTarget(
            new NockRootNode(language, builder.build(rootArgs))));
        Lazy<Battery> battery = new Lazy(() -> null);
        return Optional.of(new Formula(builder, target, battery));
      }
      catch ( ExitException e ) {
        return Optional.empty();
      }
    });
  }

  private Lazy<Optional<Core>> buildCore() {
    return new Lazy(() -> {
      try {
        return Optional.of(formula(head).battery.get().attachPayload(tail));
      }
      catch ( ExitException e ) {
        return Optional.empty();
      }
    });
  }

  private static int subMug(Object noun) {
    return ( noun instanceof ConstantCell )
      ? ((ConstantCell) noun).mug;
      : ( noun instanceof ConstantAtom )
      ? ((ConstantAtom) noun).mug;
      : Mug.get((long) noun);
  }

  private static HashCode subHash(Object noun) {
    return ( noun instanceof ConstantCell )
      ? ((ConstantCell) noun).strongHash.get()
      : ( noun instanceof ConstantAtom )
      ? ((ConstantAtom) noun).strongHash.get()
      : hashFunction.hashLong(((long) noun));
  }

  private Lazy<HashCode> buildHashCode() {
    return new Lazy(() -> hashFunction.newHasher()
      .putBytes(subHash(head).asBytes())
      .putBytes(subHash(tail).asBytes())
      .hash());
  }

  private Formula formula() throws ExitException {
    Optional<Formula> opt = formula.get();
    if ( opt.isPresent() ) {
      return opt.get();
    }
    else {
      throw ExitException("invalid formula");
    }
  }

  private static Formula formula(Object o) throws ExitException {
    return cell(o).formula();
  }

  static final class NodePair {
    final NockExpressionNode head, tail;

    NodePair(NockExpressionNode head, NockExpressionNode tail) {
      this.head = head;
      this.tail = tail;
    }
  }

  static final class FormulaPair {
    final Formula head, tail;

    FormulaPair(Formula head, Formula tail) {
      this.head = head;
      this.tail = tail;
    }

    NodePair productNodes(NBArgs a) {
      return new NodePair(head.node(a.headP()), tail.node(a.tailP()));
    }

    NodePair thenNodes(NBArgs a) {
      return new NodePair(head.node(a.headP()), tail.node(a.tail()));
    }
  }

  private FormulaPair formulas() throws ExitException {
    return new FormulaPair(formula(head), formula(tail));
  }

  private static FormulaPair formulas(Object o) throws ExitException {
    return cell(o).formulas();
  }

  private NodeBuilder parseCons() throws ExitException {
    final FormulaPair pair = formulas();
    return (a) -> {
      NodePair nodes = pair.productNodes(a);
      return axe(a, ConsNodeGen.create(nodes.head, nodes.tail);
    };
  }

  private static NockExpressionNode slotNode(Object axis) throws ExitException {
    NounLibrary axes = NounLibrary.getUncached(tail);
    return !axes.fitsInBoolean(tail)
      ? SlotNode.fromPath(axes.axisPath(tail))
      : axes.asBoolean(axis)
      ? new BailNode()
      : new IdentityNode();
  }

  private NodeBuilder parseSlot() throws ExitException {
    NockExpressionNode n = slotNode(tail);
    return (a) -> axe(a, n);
  }

  private NodeBuilder parseQuot() throws ExitException {
    final NockExpressionNode n = ( tail instanceof ConstantCell )
      ? new LiteralCellNode(tail)
      : ( tail instanceof ConstantAtom )
      ? new LiteralBigAtomNode((ConstantAtom) tail)
      : (0L == tail)
      ? new LiteralYesNode()
      : (1L == tail)
      ? new LiteralNoNode()
      : new LiteralLongNode((long) tail);
    return (a) -> axe(a, n);
  }

  private NodeBuilder parseNock() throws ExitException {
    final FormulaPair pair = formulas(tail);
    return (a) -> {
      NodePair nodes = pair.productNodes(a.tail());
      NockFunctionLookupNode
        lookup = NockFunctionLookupNodeGen.create(nodes.tail);
      NockEvalNode eval = new NockEvalNode(lookup, nodes.head);
      NockCallNode call = a.product
                        ? new NockHeadCallNode(eval)
                        : new NockTailCallNode(eval);
      return axe(a, call);
    };
  }

  private NodeBuilder parseDeep() throws ExitException {
    final Formula f = formula(tail);
    return (a) -> axe(a, DeepNodeGen.create(f.node(a.tail())));
  }

  private NodeBuilder parseBump() throws ExitException {
    final Formula f = formula(tail);
    return (a) -> axe(a, BumpNodeGen.create(f.node(a.tail())));
  }

  private NodeBuilder parseSame() throws ExitException {
    final FormulaPair pair = formulas(tail);
    return (a) -> {
      NodePair nodes = pair.productNodes(a.tail());
      return axe(a, SameNodeGen.create(nodes.head, nodes.tail));
    };
  }

  private NodeBuilder parseCond() throws ExitException {
    // as nock 6 is defined, we don't try to nock on a branch until its
    // condition is met, so a branch can be an invalid nock formula and the
    // overall 6 formula still be valid -- we treat those invalid formulas as if
    // they had been spelled [0 0].
    ConstantCell tyn = cell(tail),
                 yn  = cell(tyn.tail);
    final Formula yes, no, test = formula(tyn.head);
    final ExitException yex, nex;
    try {
      yes = formula(yn.head);
      yex = null;
    }
    catch ( ExitException e ) {
      yex = e;
    }
    try {
      no  = formula(yn.tail);
      nex = null;
    }
    catch ( ExitException e) {
      nex = e;
    }
    if ( null != yex && null != nex ) {
      throw new ExitException(yex.getMessage() + " and " + nex.getMessage());
    }
    else return (a) -> {
      NBArgs tynArgs = a.tail(), ynArgs = tynArgs.tail();
      NockExpressionNode testNode = test.node(tynArgs.headP()),
                         yesNode  = ( null == yex )
                                  ? yes.node(ynArgs.head()) 
                                  : new BailNode(),
                         noNode   = ( null == nex )
                                  ? no.node(ynArgs.tail())
                                  : new BailNode();
      return axe(a, new IfNode(testNode, yesNode, noNode));
    };
  }

  private NodeBuilder parseComp() throws ExitException {
    final FormulaPair pair = formulas(tail);
    return (a) -> {
      NodePair nodes = pair.thenNodes(a.tail());
      return axe(a, new ComposeNode(nodes.head, nodes.tail));
    };
  }

  private NodeBuilder parsePush() throws ExitException {
    final FormulaPair pair = formulas(tail);
    return (a) -> {
      NodePair nodes = pair.thenNodes(a.tail());
      return axe(a, new PushNode(nodes.head, nodes.tail));
    };
  }

  private NodeBuilder parsePull() throws ExitException {
    ConstantCell all = cell(tail);
    Object armAxis = all.head;
    NounLibrary axes = NounLibrary.getUncached(armAxis);
    final Formula core = formula(all.tail);
    if ( axes.axisInHead(axes) ) {
      final Path path = axes.axisPath(armAxis);
      return (a) -> {
        NockExpressionNode pull = PullNodeGen.create(core.node(a), path);
        return axe(a, a.product
          ? new NockHeadCallNode(pull)
          : new NockTailCallNode(pull));
      };
    }
    else {
      // Only pulls out of the battery of a core are treated as method calls,
      // pulls out of the payload get rewritten to an eval.
      return (a) -> {
        NBArgs args = a.tail();
        NockExpressionNode
          subject = axe(args.tail(), new IdentityNode())
          formula = axe(args.head(), slotNode(armAxis));

        NockFunctionLookupNode
          lookup = NockFunctionLookupNodeGen.create(formula);

        NockCallLookupNode
          eval = new NockEvalNode(lookup, subject);

        NockExpressionNode
          call = axe(a, a.tail
               ? new NockHeadCallNode(eval)
               : new NockTailCallNode(eval));

        return axe(a, new ComposeNode(core, call));
      }
    }
  }

  private NodeBuilder parseEdit() throws ExitException {
    ConstantCell all = cell(tail), spec = cell(all.tail);
    Object exis = spec.head;
    NounLibrary edits = NounLibrary.getUncached(editAxis);
    final int kind = !edits.fitsInBoolean(exis)
                   ? 2
                   : edits.asBoolean(exis) ? 0 : 1;
    final Formula large = formula(spec.tail),
                  small = formula(all.tail);

    return (a) -> {
      NBArgs args = a.tail().product();
      NockExpressionNode node,
                         largeNode = large.node(args.head().tail()),
                         smallNode = small.node(args.tail());
      switch ( kind ) {
        case 0:
          // treating edit axis 0 like [10 [0 ...] ...] like [0 0]
          node = new BailNode();
          break;
        case 1:
          // treating [10 [1 a] b] like [11 [%nop b] a]
          node = new TossNode(largeNode, smallNode);
          break;
        case 2:
          ArrayDeque<Boolean> frags = new ArrayDeque<>();
          for ( boolean f : edits.axisPath(exis) ) {
            frags.push(f);
          }
          EditPartNode chain = new EditTermNode(smallNode);
          while ( !frags.isEmpty() ) {
            chain = frags.pop()
                  ? new EditTailNode(chain)
                  : new EditHeadNode(chain);
          }
          node = new NockEditNode(largeNode, chain, exis);
          break;
        default:
          throw new AssertionError("weird kind");
      }
      return axe(a, node);
    };
  }

  private NodeBuilder parseHint() throws ExitException {
    ConstantCell all = cell(tail);
    Object hint = all.head;
    final Formula next = formula(all.tail);
    if ( hint instanceof ConstantCell ) {
      // dynamic hint
      ConstantCell chint = cell(hint);
      Object tagO = chint.head;
      Formula clue = formula(chint.tail);
      if ( !(tagO instanceof ConstantAtom) ) {
        final boolean known, nextProduct;
        final int tag = (int) tagO;
        switch ( tag ) {
          case Motes.MEMO:
          case Motes.FAST:
            known = true;
            nextProduct = true;

          default:
            known = false;
        }
        if ( known ) {
          return (a) -> {
            NBArgs args = a.tail();
            NBArgs nextArgs = args.tail();
            if ( nextProduct ) {
              nextArgs = nextArgs.product();
            }
            NockExpressionNode clueNode = clue.node(args.headP()),
                               nextNode = next.node(nextArgs),
                               node;
            switch ( tag ) {
              case Motes.MEMO:
                node = MemoNodeGen.create(clueNode, nextNode,
                  cell(chint.tail));
                break;
              case Motes.FAST:
                node = a.context.useFastHints()
                     ? FastNodeGen.create(clueNode, nextNode)
                     : new TossNode(clueNode,
                         CoreNodeGen.create(nextNode));
                break;
              default:
                throw new AssertionError("known hint not in switch");
            }
            return axe(a, node);
          };
        }
      }
      // unrecognized dynamic hints are just TossNodes
      return (a) -> {
        NBArgs args = a.tail();
        return axe(a, new TossNode(clue.node(args.headP()), 
          next.node(args.tail())));
      };
    }
    else {
      if ( !(hint instanceof ConstantAtom) ) {
        switch ( (int) hint ) {
          case Motes.CORE:
            return (a) -> axe(a,
              CoreNodegen.create(next.node(a.tail().tailP())));
        }
      }
      // unrecognized static hints are like they're not even there
      return (a) -> next.node(a.tail().tail());
    }
  }

  private NodeBuilder parseWish() throws ExitException {
    throw new ExitException("wish not implemented");
  }
}
