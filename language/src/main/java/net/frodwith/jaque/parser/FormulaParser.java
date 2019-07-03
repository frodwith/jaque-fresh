package net.frodwith.jaque.parser;

import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.util.AxisBuilder;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.library.NounLibrary;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.nodes.*;

public final class FormulaParser {
  private static NounLibrary nouns = NounLibrary.getUncached();

  private static NockExpressionNode axe(AxisBuilder axis, NockExpressionNode node) {
    node.setAxisInFormula(axis.write());
    return node;
  }

  private static Function<AstContext,NockExpressionNode> wrap(NockExpressionNode e) {
    return (c) -> e;
  }

  private static Function<AstContext,NockExpressionNode>
    parseCons(Object headNoun, Object tailNoun, AxisBuilder axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode>
      head = parseExpr(headNoun, axis.head(), false),
      tail = parseExpr(tailNoun, axis.tail(), false);

    return (c) -> axe(axis, ConsNodeGen.create(head.apply(c), tail.apply(c)));
  }

  private static NockExpressionNode parseSlot(Object arg) throws ExitException {
    return !nouns.fitsInBoolean(arg)
      ? SlotNode.fromPath(nouns.axisPath(arg))
      : nouns.asBoolean(arg)
      ? new BailNode()
      : new IdentityNode();
  }

  private static NockExpressionNode parseQuot(Object arg) {
    return ( arg instanceof Cell )
      ? new LiteralCellNode((Cell) arg)
      : ( arg instanceof BigAtom )
      ? new LiteralBigAtomNode((BigAtom) arg)
      : new LiteralLongNode((long) arg);
  }

  private static Function<AstContext,NockExpressionNode>
    parseEval(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);

    Function<AstContext,NockExpressionNode>
      subject = parseExpr(args.head, axis.tail().head(), false),
      formula = parseExpr(args.tail, axis.tail().tail(), false);

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
    parseUnary(Object arg, AxisBuilder axis)
      throws ExitException {
    return parseExpr(arg, axis.tail(), false);
  }

  private static Function<AstContext,NockExpressionNode>
    parseDeep(Object arg, AxisBuilder axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode> e = parseUnary(arg, axis);
    return (c) -> axe(axis, DeepNodeGen.create(e.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseBump(Object arg, AxisBuilder axis)
      throws ExitException {
    Function<AstContext,NockExpressionNode> e = parseUnary(arg, axis);
    return (c) -> axe(axis, BumpNodeGen.create(e.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseSame(Object arg, AxisBuilder axis)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      left = parseExpr(args.head, axis.tail().head(), false),
      right = parseExpr(args.tail, axis.tail().tail(), false);

    return (c) -> axe(axis, SameNodeGen.create(left.apply(c), right.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parseIf(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Trel args = Trel.require(arg);

    Function<AstContext,NockExpressionNode> yes, no,
      test = parseExpr(args.p, axis.tail().head(), false);

    ExitException yex = null, nex = null;

    try {
      yes = parseExpr(args.q, axis.tail().tail().head(), tail);
    }
    catch ( ExitException e ) {
      yex = e;
      yes = wrap(new BailNode());
    }
    try {
      no  = parseExpr(args.r, axis.tail().tail().tail(), tail);
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
    parseComp(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      f = parseExpr(args.head, axis.tail().head(), false),
      g = parseExpr(args.tail, axis.tail().tail(), tail);

    return (c) -> axe(axis, new ComposeNode(f.apply(c), g.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parsePush(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Function<AstContext,NockExpressionNode>
      f = parseExpr(args.head, axis.tail().head(), false),
      g = parseExpr(args.tail, axis.tail().tail(), tail);
    return (c) -> axe(axis, new PushNode(f.apply(c), g.apply(c)));
  }

  private static Function<AstContext,NockExpressionNode>
    parsePull(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Axis armAxis = Axis.require(args.head);
    AxisBuilder coreAxis = axis.tail().tail();

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
        formula = axe(axis.tail().head(), parseSlot(armAxis));

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
    parseStaticHint(Object tag, Object nextNoun, AxisBuilder axis, AxisBuilder nextAxis, boolean tail)
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
    parseDynamicHint(Cell hints, Object nextNoun, AxisBuilder axis, AxisBuilder nextAxis, boolean tail)
      throws ExitException {
    Function<AstContext, NockExpressionNode> next,
      clue = parseExpr(hints.tail, axis.tail().head(), false);

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
    parseHint(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Object hint = args.head;
    Object nextNoun = args.tail;
    AxisBuilder nextAxis = axis.tail().tail();

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
    parseEdit(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Cell spec = Cell.require(args.head);
    Object editAxis = spec.head;
    if ( !nouns.isAtom(editAxis) ) {
      throw new ExitException("non-atomic edit axis");
    }
    Function<AstContext,NockExpressionNode>
      small = parseExpr(spec.tail, axis.tail().head().tail(), false),
      large = parseExpr(args.tail, axis.tail().tail(), false);

    if ( nouns.fitsInBoolean(editAxis) ) {
      if ( nouns.asBoolean(editAxis) ) {
        // Editing axis 0 should rightly be regarded as a crash node like [0 0]
        return wrap(axe(axis, new BailNode()));
      }
      else {
        // NockEditNode specializes to producing a cell, but edit 1 is valid
        // and could produce an atom.
        return (c) -> axe(axis, new TossNode(large.apply(c), small.apply(c)));
      }
    }
    else {
      ArrayDeque<Boolean> frags = new ArrayDeque<>();
      for ( boolean f : nouns.axisPath(editAxis) ) {
        frags.push(f);
      }
      return (c) -> {
        EditPartNode chain = new EditTermNode(small.apply(c));

        while ( !frags.isEmpty() ) {
          chain = frags.pop()
                ? new EditTailNode(chain)
                : new EditHeadNode(chain);
        }

        return axe(axis,
          new NockEditNode(large.apply(c), chain, editAxis, c.dashboard));
      };
    }
  }

  private static Function<AstContext,NockExpressionNode>
    parseExpr(Object formula, AxisBuilder axis, boolean tail)
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
          return wrap(axe(axis,parseSlot(arg)));
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
    CompilerAsserts.neverPartOfCompilation();
    Function<AstContext,NockExpressionNode>
      exprFactory = parseExpr(formula, AxisBuilder.EMPTY, true);

    return (c) -> Truffle.getRuntime().createCallTarget(
      new NockRootNode(c.language, sup, exprFactory.apply(c)));
  }

  @TruffleBoundary
  public static Function<AstContext,RootCallTarget>
    parseMapped(SourceMappedNoun mapped)
      throws ExitException {
    return factory(mapped.noun, () -> mapped);
  }

  @TruffleBoundary
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
}
