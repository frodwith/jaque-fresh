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
import net.frodwith.jaque.nodes.expression.*;

public final class FormulaParser {

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
      : new SlotExpressionNode(axis);
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
    return (c) -> axe(axis, BumpExpressionNodeGen.create(e.apply(c)));
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

      case Motes.SLOG: {
        next = parseExpr(nextNoun, nextAxis, false);
        return (c) -> new SlogNode(clue.apply(c), next.apply(c));
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

  private static Function<AstContext,NockExpressionNode>
    parseWish(Object arg, Axis axis)
      throws ExitException {
    Cell args = Cell.require(arg);

    Function<AstContext,NockExpressionNode>
      left = parseExpr(args.head, axis.peg(6), false),
      right = parseExpr(args.tail, axis.peg(7), false);

    return (c) -> axe(axis,
      new WishNode(c, left.apply(c), right.apply(c)));
  }

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
      ArrayDeque<Boolean> path = new ArrayDeque<>();
      for ( boolean right : editAxis ) {
        path.push(right);
      }
      return (c) -> {
        EditPartNode chain = new EditTermNode(small.apply(c));

        while ( !path.isEmpty() ) {
          chain = path.pop()
                ? new EditTailNode(chain)
                : new EditHeadNode(chain);
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
        case 12:
          return parseWish(arg, axis);
        default:
          throw new ExitException("bad opcode: " + code);
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
}
