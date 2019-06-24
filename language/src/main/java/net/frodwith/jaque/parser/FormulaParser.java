package net.frodwith.jaque.parser;

import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.nodes.*;

public final class FormulaParser {
  private final NounLibrary nouns;
  private NockLanguage language;

  public FormulaParser(NockLanguage language) {
    this.nouns = NounLibrary.getUncached();
    this.language = language;
  }

  public static RootCallTarget parse(Object noun) throws ExitException {
    parseExpr(noun, null, true);
  }

  private static final class AxisPart {
    static final AxisPart EMPTY = new AxisPart(null);
    final boolean right;
    final AxisPart next;

    AxisPart(boolean right, AxisPart next) {
      this.right = right;
      this.next = next;
    }

    AxisPart add(boolean right) {
      return new AxisPart(right, this);
    }

    NockExpressionNode store(NockExpressionNode node) {
      // parts are layed out in path order (go right, then left, then right,
      // etc)
      // start by putting a 1 on
      // keep shifting left (increment if right)
      // when you get to a full byte, add to the bytes list
      //   we want little endian, which means we add to the end of the list
      //   (bits get less significant as we go, the least significant bit being
      //   the last element in the path)
      // we need to know the length actually before we pack the suckers.
      AxisPart p;
      byte buf;
      byte bits;
      for ( bits = 1, buf = 1, p = this; p != null; p = p.next, ++bits ) {
        if ( 8 == bits ) {
          bits = 0;
          bytes.append(buf);
        }
        buf <<= 1;
        if ( p.right ) {
          buf &= 1;
        }
      }



      byte buf = 1;
      AxisPart p = this;
      while ( 


      final byte[] bits = new byte[8];
      ArrayDeque<Byte> bytes = new ArrayDeque<>();
      int i;
      for ( i = 0, p = this; 
            p != null;
            p = p.next, i = ( 7 == i ) ? 
        bits[i] = p.right << i;
      }
    }
  }

  private NockExpressionNode parseExpr(Object noun, AxisPart ax, boolean tail)
    throws ExitException {
    Object op = nouns.head(noun),
           arg = nouns.tail(noun);

    if ( nouns.isCell(op) ) {
      return parseCons(op, arg, axis);
    }
    else switch ( nouns.asInt(op) ) {
      case 0:
        return parseSlot(arg, axis);
      case 1:
        return parseQuot(arg, axis);
      case 2:
        return parseEval(arg, axis, tail);
      case 3:
        return parseDeep(arg, axis);
      case 4:
        return parseBump(arg, axis);
      case 5:
        return parseSame(arg, axis);
      case 6:
        return parseIf(arg, axis, tail);
      case 7:
        return parseComp(arg, axis, tail);
      case 8:
        return parsePush(arg, axis, tail);
      case 9:
        return parsePull(arg, axis, tail);
      case 10:
        return parseEdit(arg, axis, tail);
      case 11:
        return parseHint(arg, axis, tail);
      //case 12:
      //  return parseWish(arg);
      }
    }
    throw new ExitException("invalid formula");
  }


  public static RootCallTarget parse(Object noun) throws ExitException {
    FormulaParser p = new FormulaParser(language);
    NockExpressionNode body = p.parseExpr(noun, null, true);

    if ( !nouns.isCell(noun) ) {
      throw new ExitException();
    }
    else {
      Object op = nouns.head(noun),
             arg = nouns.tail(noun);
      if ( nouns.isCell(op) ) {

      }
    }
  }


  private static NockExpressionNode axe(Axis axis, NockExpressionNode node) {
    node.setAxisInFormula(axis);
    return node;
  }

  private static Function<AstContext,NockExpressionNode> wrap(NockExpressionNode e) {
    return (c) -> e;
  }

  private static Function<AstContext,NockExpressionNode>
    parseCons(Object headNoun, Object tailNoun, Axis axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode>
      head = parseExpr(headNoun, axis.peg(2), false),
      tail = parseExpr(tailNoun, axis.peg(3), false);

    return (c) -> axe(axis, ConsNodeGen.create(head.apply(c), tail.apply(c)));
  }

  private static NockExpressionNode parseSlot(Axis axis) {
    return axis.isCrash()
      ? new BailNode()
      : axis.isIdentity()
      ? new IdentityNode()
      : new SlotNode(axis);
  }

  private static NockExpressionNode parseQuot(Object arg) {
    return ( arg instanceof Cell )
      ? new LiteralCellNode((Cell) arg)
      : ( arg instanceof BigAtom )
      ? new LiteralBigAtomNode((BigAtom) arg)
      : new LiteralLongNode((long) arg);
  }

  private static Function<AstContext,NockExpressionNode>
    parseEval(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);

    Function<AstContext,NockExpressionNode>
      subject = parseExpr(args.head, axis.peg(6), false),
      formula = parseExpr(args.tail, axis.peg(7), false);

    return (c) -> {
      NockExpressionNode e = formula.apply(c);
      NockFunctionLookupNode lookup = NockFunctionLookupNodeGen.create(e, c);
      NockEvalNode eval = new NockEvalNode(lookup, subject.apply(c));
      return axe(axis, tail
        ? new NockTailCallNode(eval)
        : new NockHeadCallNode(eval));
    };
  }

  private static Function<AstContext,NockExpressionNode>
    parseUnary(Object arg, Axis axis)
      throws ExitException {
    return parseExpr(arg, axis.peg(3), false);
  }

  private static Function<AstContext,NockExpressionNode>
    parseDeep(Object arg, Axis axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode> e = parseUnary(arg, axis);
    return (c) -> axe(axis, DeepNodeGen.create(e.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseBump(Object arg, Axis axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode> e = parseUnary(arg, axis);
    return (c) -> axe(axis, BumpNodeGen.create(e.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseSame(Object arg, Axis axis)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      left = parseExpr(args.head, axis.peg(6), false),
      right = parseExpr(args.tail, axis.peg(7), false);

    return (c) -> axe(axis, SameNodeGen.create(left.apply(c), right.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseIf(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Trel args = Trel.require(arg);

    Function<AstContext,NockExpressionNode> yes, no,
      test = parseExpr(args.p, axis.peg(6), false);

    ExitException yex = null, nex = null;

    try {
      yes = parseExpr(args.q, axis.peg(14), tail);
    }
    catch ( ExitException e ) {
      yex = e;
      yes = wrap(new BailNode());
    }
    try {
      no  = parseExpr(args.r, axis.peg(15), tail);
    }
    catch ( ExitException e) {
      nex = e;
      no  = wrap(new BailNode());
    }

    if ( null != yex && null != nex ) {
      throw new ExitException(yex.getMessage() + " and " + nex.getMessage());
    }
    else {
      final Function<AstContext,NockExpressionNode> y = yes, n = no;
      return (c) -> axe(axis,
        new IfNode(test.apply(c), y.apply(c), n.apply(c)));
    }
  }

  private static Function<AstContext,NockExpressionNode>
    parseComp(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      f = parseExpr(args.head, axis.peg(6), false),
      g = parseExpr(args.tail, axis.peg(7), tail);

    return (c) -> axe(axis, new ComposeNode(f.apply(c), g.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parsePush(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      f = parseExpr(args.head, axis.peg(6), false),
      g = parseExpr(args.tail, axis.peg(7), tail);
    return (c) -> axe(axis, new PushNode(f.apply(c), g.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parsePull(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Axis armAxis = Axis.require(args.head);
    Axis coreAxis = axis.peg(7);

    Function<AstContext,NockExpressionNode> core =
      parseExpr(args.tail, coreAxis, false);

    if ( armAxis.inHead() ) {
      return (c) -> {
        NockCallLookupNode pull =
          PullNodeGen.create(core.apply(c), armAxis, c);

        return axe(axis, tail
          ? new NockTailCallNode(pull)
          : new NockHeadCallNode(pull));
      };
    }
    else {
      // Only pulls out of the battery of a core are treated as method calls,
      // pulls out of the payload get rewritten to an eval.
      NockExpressionNode
        subject = axe(coreAxis, new IdentityNode()),
        formula = axe(axis.peg(6), parseSlot(armAxis));

      return (c) -> {
        NockFunctionLookupNode
          lookup = NockFunctionLookupNodeGen.create(formula, c);

        NockCallLookupNode
          eval = new NockEvalNode(lookup, subject);
        NockExpressionNode
          call = axe(axis, tail
               ? new NockHeadCallNode(eval)
               : new NockTailCallNode(eval));

        return axe(axis, new ComposeNode(core.apply(c), call));
      };
    }
  }

  private static Function<AstContext,NockExpressionNode>
    parseStaticHint(Object tag, Object nextNoun, Axis axis, Axis nextAxis, boolean tail)
      throws ExitException {
    Function<AstContext,NockExpressionNode> next;

    try {
      switch ( Atom.requireInt(tag) ) {
        case Motes.CORE:
          next = parseExpr(nextNoun, nextAxis, false);
          return (c) -> CoreNodeGen.create(next.apply(c), c.dashboard);
      }
    }
    catch ( ExitException e ) {
    }

    return parseExpr(nextNoun, nextAxis, tail);
  }

  private static Function<AstContext,NockExpressionNode>
    parseDynamicHint(Cell hints, Object nextNoun, Axis axis, Axis nextAxis, boolean tail)
      throws ExitException {
    Function<AstContext, NockExpressionNode> next,
      clue = parseExpr(hints.tail, axis.peg(6), false);

    int tag;
    try {
      // all currently recognized hint tags fit into an int mote, which
      // is handy for switch statements.
      tag = Atom.requireInt(hints.head);
    }
    catch ( ExitException e ) {
      next = parseExpr(nextNoun, nextAxis, tail);
      return (c) -> axe(axis, new TossNode(clue.apply(c), next.apply(c)));
    }
    switch ( tag ) {
      case Motes.MEMO: {
        next = parseExpr(nextNoun, nextAxis, false);
        Cell key = Cell.require(nextNoun);
        return (c) -> {
          return axe(axis, new MemoNode(
            c.language.getContextReference(),
            key,
            clue.apply(c),
            next.apply(c)));
        };
      }

      case Motes.FAST: {
        next    = parseExpr(nextNoun, nextAxis, false);
        return (c) -> {
          NockExpressionNode clueNode = clue.apply(c);
          return axe(axis, c.dashboard.fastHints
            ? new FastNode(c.dashboard, clueNode, next.apply(c))
            : new TossNode(clueNode,
                CoreNodeGen.create(next.apply(c), c.dashboard)));
        };
      }

      default:
        next = parseExpr(nextNoun, nextAxis, tail);
        return (c) -> axe(axis, new TossNode(clue.apply(c), next.apply(c)));
      /*
      case Motes.MEAN:
      case Motes.HUNK:
      case Motes.LOSE:
      case Motes.SPOT:
        return StackNode.create(hints.head, clue, parseExpr(next, false));

      case Motes.SLOG:
        return SlogNode.create(clue, parseExpr(next, tail));
      */
    }
  }

  private static Function<AstContext,NockExpressionNode>
    parseHint(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Object hint = args.head;
    Object nextNoun = args.tail;
    Axis nextAxis = axis.peg(7);

    return ( args.head instanceof Cell )
      ? parseDynamicHint((Cell) hint, nextNoun, axis, nextAxis, tail)
      : parseStaticHint(hint, nextNoun, axis, nextAxis, tail);
  }

  /*

  private static WishNode parseWish(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    ExpressionNode ref = parseExpr(args.head, false);
    ExpressionNode gof = parseExpr(args.tail, false);

    return tail
      ? TailWishNode.create(ref, gof)
      : HeadWishNode.create(ref, gof);
  }

*/

  private static Function<AstContext,NockExpressionNode>
    parseEdit(Object arg, Axis axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Cell spec = Cell.require(args.head);
    Axis editAxis = Axis.require(spec.head);
    Function<AstContext,NockExpressionNode>
      small = parseExpr(spec.tail, axis.peg(13), false),
      large = parseExpr(args.tail, axis.peg(7), false);

    if ( editAxis.isIdentity() ) {
      // NockEditNode specializes to producing a cell, but edit 1 is valid
      // and could produce an atom.
      return (c) -> axe(axis, new TossNode(large.apply(c), small.apply(c)));
    }
    else {
      ArrayDeque<Axis.Fragment> frags = new ArrayDeque<>();
      for ( Axis.Fragment f : editAxis ) {
        frags.push(f);
      }
      return (c) -> {
        EditPartNode chain = new EditTermNode(small.apply(c));

        while ( !frags.isEmpty() ) {
          chain = ( frags.pop() == Axis.Fragment.HEAD )
                ? new EditHeadNode(chain)
                : new EditTailNode(chain);
        }

        return axe(axis,
          new NockEditNode(large.apply(c), chain, editAxis, c.dashboard));
      };
    }
  }

  private static Function<AstContext,NockExpressionNode>
    parseExpr(Object formula, Axis axis, boolean tail)
      throws ExitException {
    Cell c = Cell.require(formula);
    Object op   = c.head,
           arg  = c.tail;

    if ( op instanceof Cell ) {
      return parseCons(op, arg, axis);
    }
    else {
      int code = Atom.requireInt(op);
      switch ( code ) {
        case 0:
          return wrap(axe(axis,parseSlot(Axis.require(arg))));
        case 1:
          return wrap(axe(axis,parseQuot(arg)));
        case 2:
          return parseEval(arg, axis, tail);
        case 3:
          return parseDeep(arg, axis);
        case 4:
          return parseBump(arg, axis);
        case 5:
          return parseSame(arg, axis);
        case 6:
          return parseIf(arg, axis, tail);
        case 7:
          return parseComp(arg, axis, tail);
        case 8:
          return parsePush(arg, axis, tail);
        case 9:
          return parsePull(arg, axis, tail);
        case 10:
          return parseEdit(arg, axis, tail);
        case 11:
          return parseHint(arg, axis, tail);
          /*
             case 12:
               return parseWish(arg);
           */
        default:
          throw new ExitException("bad opcode");
      }
    }
  }

  private static Function<AstContext,RootCallTarget>
    factory(Object formula, Supplier<SourceMappedNoun> sup)
      throws ExitException {
    Function<AstContext,NockExpressionNode>
      exprFactory = parseExpr(formula, Axis.IDENTITY, true);

    return (c) -> Truffle.getRuntime().createCallTarget(
      new NockRootNode(c.language, sup, exprFactory.apply(c)));
  }

  public static Function<AstContext,RootCallTarget>
    parseMapped(SourceMappedNoun mapped)
      throws ExitException {
    return factory(mapped.noun, () -> mapped);
  }

  public static Function<AstContext,RootCallTarget>
    parse(Object formula)
      throws ExitException {
    Cell c = Cell.require(formula);

    return factory(formula, () -> {
      try {
        return SourceMappedNoun.fromCell(c);
      }
      catch ( ExitException e ) {
        throw new RuntimeException("NockFunction.fromCell:supplier", e);
      }
    });
  }

  private final NounLibrary nouns;
  private final NockLanguage language;

  private FormulaParser(NockLanguage language) {
    this.nouns = NounLibrary.getUncached();
    this.language = language;
  }

  private NockExpressionNode axe(NockExpressionNode node, Axis axis) {
    //  ... *sigh*. axis representation. it isn't a noun, so that's fine...
    //  seems like it does need a NounLibrary to interact with its internal atom
    //  though. need a solution here.
    //  one solution would be to represent Axis internally as an array of
    //  booleans, or maybe a byte array. booleans would be convenient but waste
    //  space, a byte array would be consistent, handy, and non space-wasting.
    //  also could use a different encoding, like a singleton for
    //  identity/crash, and then a different packing of path elements. meh.

    // like null == crash
    // null byte[] == identity
    // otherwise it's a byte[] that you read the bits of right-to-left.

    // Could also do List<Boolean>, that would allow for easy
    // construction/deconstruction. Doesn't make a terribly good map key, but
    // you could use the atoms for those, presumably.

    // how are we going to do non-message equality, also? just uncached
    // libraries? (guess it depends on whether we're in a node...)
  }

  private NockExpressionNode parseCons(Object op, Object arg, Axis axis) {
    NockExpressionNode headNode = parseExpr(op, axis.peg(2), false),
                       tailNode = parseExpr(arg, axis.peg(3), false);
    NockExpressionNode consNode = ConsNodeGen.create(headNode, tailNode);
    consNode.setAxisInFormula(axis);
    return consNode;
  }

  private static NockExpressionNode parseSlot(Object arg) throws ExitException {
    if ( !nouns.isAtom(arg) ) {
      throw new ExitException();
    }
    else if ( nouns.fitsInInt(arg) ) {
      switch ( nouns.asInt(arg) ) {
        case 0:
          return new BailNode();
        case 1:
          return new IdentityNode();
      }
    }
    return new SlotNode(FragmentNode.fromBits(noun.asBits(arg)));
  }

  private NockExpressionNode parseExpr(Object noun, Axis axis, boolean tail)
    throws ExitException {
    Object op = nouns.head(noun),
           arg = nouns.tail(noun);

    if ( nouns.isCell(op) ) {
      return parseCons(op, arg, axis);
    }
    else if ( nouns.fitsInInt(op) ) {
      switch ( nouns.asInt(op) ) {
        case 0:
          return parseSlot(arg, axis);
        case 1:
          return parseQuot(arg, axis);
        case 2:
          return parseEval(arg, axis, tail);
        case 3:
          return parseDeep(arg, axis);
        case 4:
          return parseBump(arg, axis);
        case 5:
          return parseSame(arg, axis);
        case 6:
          return parseIf(arg, axis, tail);
        case 7:
          return parseComp(arg, axis, tail);
        case 8:
          return parsePush(arg, axis, tail);
        case 9:
          return parsePull(arg, axis, tail);
        case 10:
          return parseEdit(arg, axis, tail);
        case 11:
          return parseHint(arg, axis, tail);
        /*
           case 12:
             return parseWish(arg);
         */
      }
    }
    throw new ExitException("invalid formula");
  }

  public static RootCallTarget parse(NockLanguage language, Object noun)
    throws ExitException {
    FormulaParser p = new FormulaParser(language);
    NockExpressionNode body = p.parseExpr(noun, Axis.IDENTITY, true);

    if ( !nouns.isCell(noun) ) {
      throw new ExitException();
    }
    else {
      Object op = nouns.head(noun),
             arg = nouns.tail(noun);
      if ( nouns.isCell(op) ) {

      }
    }
  }
}
