package net.frodwith.jaque.data;

// what if the parser only worked on ConstantCells? bit of a bootstrapping
// conundrum, but would save the context.intern call... and would probably be
// faster.

@ExportLibrary(NounLibrary.class)
public final class ConstantCell {
  final Object head, tail;
  final int mug;
  final Lazy<HashCode> strongHash;
  final Lazy<Optional<Formula>> formula;
  final Lazy<Optional<Core>> core;

  public ConstantCell(NockLanguage language, Object head, Object tail) {
    this.head = head;
    this.tail = tail;
    this.mug = Mug.both(subMug(head), subMug(tail));
    this.formula = new Lazy(() ->
      Formula.build(new FormulaParser(language), this));
    this.core = new Lazy(() -> {
      Optional<Formula> bf = getFormula(head);
      return bf.isPresent()
        ? Optional.of(bf.get().battery.get().attachPayload(tail));
        : Optional.empty();
    });
  }

  private Optional<Formula> getFormula(Object object) {
    return ( object instanceof ConstantCell )
      ? ((ConstantCell) object).formula.get()
      : Optional.empty();
  }

  private int subMug(Object noun) {
    return ( noun instanceof ConstantCell )
      ? ((ConstantCell) noun).mug;
      : ( noun instanceof ConstantAtom )
      ? ((ConstantAtom) noun).mug;
      : Mug.get((long) noun);
  }

  public static ConstantCell cons(NockLanguage language, Object head, Object tail) {
    return cons(head, tail, 0);
  }

  /*
  public static ConstantCell cons(NockLanguage language, Object head, Object tail, int mug) {
    if ( 0 == mug ) {
    }


    Lazy<Optional<Formula>> formula = Formula.promise(language, 
      Optional<Formula> hf = getFormula(head),
                        tf = getFormula(tail);

      if ( hf.isPresent() ) {
        if ( tf.isPresent() ) {
          formula = formulize(() -> ConsNodeGen.create(
            hf.get().factory.get(),
            tf.get().factory.get()));
        }
        else {
          return Optional.empty();
        }
      }
      else if ( !(head instanceof ConstantAtom) ) {
        switch ( (int) head ) {
          case 0:
            formula = formulize(() -> null);
            break;
          case 1:
            formula = formulize(() -> null);
            break;
          case 2:
            formula = formulize(() -> null);
            break;
          case 3:
            formula = formulize(() -> null);
            break;
          case 4:
            formula = formulize(() -> null);
            break;
          case 5:
            formula = formulize(() -> null);
            break;
          case 6:
            formula = formulize(() -> null);
            break;
          case 7:
            formula = formulize(() -> null);
            break;
          case 8:
            formula = formulize(() -> null);
            break;
          case 9:
            formula = formulize(() -> null);
            break;
          case 10:
            formula = formulize(() -> null);
            break;
          case 11:
            formula = formulize(() -> null);
            break;
          case 12:
            formula = formulize(() -> null);
            break;
        }
      }
    });

    return new ConstantCell(head, tail, mug, formula, core);




    Optional<Formula> formula = Optional.empty();

  {}
*/

  static final class NBArgs {
    final NockLanguage language;
    final Lazy<SourceMappedNoun> mappedFormula;
    final AxisBuilder axis;
    final boolean tail, fast;

    NBArgs(NockLanguage language,
                  Lazy<SourceMappedNoun> mappedFormula, 
                  AxisBuilder axis, 
                  boolean tail,
                  boolean fast) {
    }

    NBArgs axis(AxisBuilder axis) {
      return new NBArgs(language, mappedFormula, axis, tail, fast);
    }

    NBArgs head() {
      return axis(axis.head());
    }

    NBArgs tail() {
      return axis(axis.tail());
    }

    NBArgs product() {
      return !tail ? this :
        new NBArgs(language, mappedFormula, axis.tail(), false, fast);
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
    final Lazy<RootCallTarget> target;
    final Lazy<Battery> battery;

    private Formula(NodeBuilder builder
                    Lazy<RootCallTarget> target,
                    Lazy<Battery> battery) {
      this.builder = builder;
      this.target = target;
      this.battery = battery;
    }

    NockExpressionNode node(NBArgs a) {
      return builder.build(a);
    }

    static Optional<Formula>
      build(FormulaParser parser, ConstantCell cell) {
      Optional<Supplier<NockExpressionNode>> f = parser.parse(cell);
      if ( !f.isPresent() ) {
        return Optional.empty();
      }
      else {
        final Supplier<NockExpressionNode> factory = f.get();
        final Lazy<RootCallTarget> target = new Lazy(() -> {
          NockRootNode rootNode = new NockRootNode(language, factory.get());
          return Truffle.getRuntime.createCallTarget(rootNode);
        });
        final Lazy<Battery> battery = new Lazy(() -> {
          return null;
        });
        return Optional.of(new Formula(factory, target, battery));
      }
    }
  }

  private static Formula formula(Object o) throws ExitException {
    if ( o instanceof ConstantCell ) {
      Optional<Formula> opt = ((ConstantCell) o).formula.get();
      if ( opt.isPresent() ) {
        return opt.get();
      }
    }
    throw ExitException("invalid formula");
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
      a = a.product();
      return new NodePair(head.node(a.head()), tail.node(a.tail()));
    }

    NodePair thenNodes(NBArgs a) {
      return new NodePair(head.node(a.head().product()), tail.node(a.tail()));
    }
  }

  private static FormulaPair formulaPair(ConstantCell cell) throws ExitException {
    return new FormulaPair(formula(cell.head), formula(cell.tail));
  }

  private static FormulaBuilder parseSlot(Object axis) throws ExitException {
    NounLibrary axes = NounLibrary.getUncached(axis);
    NockExpressionNode n = !axes.fitsInBoolean(axis)
      ? SlotNode.fromPath(axes.axisPath(axis))
      : axes.asBoolean(axis)
      ? new BailNode()
      : new IdentityNode();
    return (a) -> axe(a, n);
  }

  private NodeBuilder parse() throws ExitException {
    if ( head instanceof ConstantCell ) {
      FormulaPair pair = formulaPair((ConstantCell) head);
      return (a) -> {
        NodePair nodes = pair.productNodes(a);
        return axe(a, ConsNodeGen.create(nodes.head, nodes.tail);
      };
    }
    else if ( !(head instanceof ConstantAtom) ) {
      switch ( (int) head ) {
        case 0: {
          return parseSlot(tail);
        }
        case 1: {
          return ( tail instanceof ConstantCell )
            ? new LiteralCellNode(tail)
            : ( tail instanceof ConstantAtom )
            ? new LiteralBigAtomNode((ConstantAtom) tail)
            : (0L == tail)
            ? new LiteralYesNode()
            : (1L == tail)
            ? new LiteralNoNode()
            : new LiteralLongNode((long) tail);
        }
        case 2: {
          FormulaPair pair = formulaPair(cell(tail));

          return (a) -> {
            NodePair nodes = pair.productNodes(a.tail());
            NockFunctionLookupNode
              lookup = NockFunctionLookupNodeGen.create(nodes.tail);
            NockEvalNode eval = new NockEvalNode(lookup, nodes.head);
            NockCallNode call = a.tail
                              ? new NockTailCallNode(eval)
                              : new NockHeadCallNode(eval);
            return axe(a, call);
          };
        }
        case 3: {
          Formula f = formula(tail);
          return (a) -> axe(a, DeepNodeGen.create(f.node(a.tail())));
        }
        case 4: {
          Formula f = formula(tail);
          return (a) -> axe(a, BumpNodeGen.create(f.node(a.tail())));
        }
        case 5: {
          FormulaPair pair = formulaPair(cell(tail));
          return (a) -> {
            NodePair nodes = pair.productNodes(a.tail());
            return axe(a, SameNodeGen.create(nodes.head, nodes.tail));
          };
        }
        case 6: {
          ConstantCell all = cell(tail),
                       yn  = cell(all.tail);
          Formula yes, no, test = formula(all.head);
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
            NBArgs allArgs = a.tail(), branchArgs = allArgs.tail();
            NockExpressionNode testNode = test.node(allArgs.head().product()),
                               yesNode  = ( null == yex )
                                        ? yes.node(branchArgs.head()) 
                                        : new BailNode(),
                               noNode   = ( null == nex )
                                        ? no.node(branchArgs.tail())
                                        : new BailNode();
            return axe(a, new IfNode(testNode, yesNode, noNode));
          };
        }
        case 7:
          FormulaPair pair = formulaPair(cell(tail));
          return (a) -> {
            NodePair nodes = pair.thenNodes(a.tail());
            return axe(a, new ComposeNode(nodes.head, nodes.tail));
          };
        }
        case 8: {
          FormulaPair pair = formulaPair(cell(tail));
          return (a) -> {
            NodePair nodes = pair.thenNodes(a.tail());
            return axe(a, new PushNode(nodes.head, nodes.tail));
          };
        }
        case 9: {
          ConstantCell all = cell(tail);
          Formula core = formula(all.tail);
          Object armAxis = all.head;
          NounLibrary axes = NounLibrary.getUncached(armAxis);
          if ( axes.axisInHead(axes) ) {
            final Path path = axes.axisPath(armAxis);
            return (a) -> {
              NockExpressionNode pull = PullNodeGen.create(core.node(a), path);
              return axe(a, a.tail
                ? new NockTailCallNode(pull)
                : new NockHeadCallNode(pull));
            };
          }
          else {
            // Only pulls out of the battery of a core are treated as method calls,
            // pulls out of the payload get rewritten to an eval.
            return (a) -> {
              NBArgs args = a.tail();
              NockExpressionNode
                subject = axe(args.tail(), new IdentityNode())
                formula = axe(args.head(), parseSlot(armAxis));

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
        case 10: {
          ConstantCell all = cell(tail), spec = cell(all.tail);
          Object exis = spec.head;
          NounLibrary edits = NounLibrary.getUncached(editAxis);
          Formula large = formula(spec.tail),
                  small = formula(all.tail);
          int kind = !edits.fitsInBoolean(exis) ? 2 : edits.asBoolean(exis) ? 0 : 1;

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
        case 11: {
          ConstantCell all = cell(tail);
          Object hint = all.head;
          Formula next = formula(all.tail);
          if ( hint instanceof ConstantCell ) {
            // dynamic hint
            ConstantCell chint = cell(hint);
            Object tagO = chint.head;
            Formula clue = formula(chint.tail);
            if ( !(tagO instanceof ConstantAtom) ) {
              boolean known, nextProduct;
              int tag = (int) tagO;
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
                  NockExpressionNode clueNode = clue.node(args.head().product()),
                                     nextNode = next.node(nextArgs),
                                     node;
                  switch ( tag ) {
                    case Motes.MEMO:
                      node = MemoNodeGen.create(clueNode, nextNode,
                        cell(chint.tail));
                      break;
                    case Motes.FAST:
                      node = a.fast
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
              return axe(a, new TossNode(clue.node(args.head().product()), 
                next.node(args.tail())));
            };
          }
          else {
            if ( !(hint instanceof ConstantAtom) ) {
              switch ( (int) hint ) {
                case Motes.CORE:
                  return (a) -> axe(a,
                    CoreNodegen.create(next.node(a.tail().tail().product())));
              }
            }
            // unrecognized static hints are like they're not even there
            return (a) -> next.node(a.tail().tail());
          }
        }
        case 12: {
          // wish not yet implemented
        }
      }
    }
    throw new ExitException("invalid formula");
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
