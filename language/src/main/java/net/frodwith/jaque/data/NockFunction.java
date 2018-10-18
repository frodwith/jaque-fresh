package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;

import net.frodwith.jaque.nodes.*;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.exception.ExitException;


import static net.frodwith.jaque.runtime.HoonMath.peg;

// A NockFunction represents the mathematical idea of a function picked out by
// a nock formula i.e. it represents the partial application nock(_,formula).
// Metaphorically, this is part of the plumbing for a nock 2 operation. In
// particular, jet handling is not modeled here, but in NockObject (cores, nock 9).
//
public final class NockFunction implements TruffleObject {
  public final RootCallTarget callTarget;

  public NockFunction(RootCallTarget callTarget) {
    this.callTarget = callTarget;
  }

  public static RootCallTarget cellTarget(NockLanguage language, Cell formula)
    throws ExitException {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      try {
        return SourceMappedNoun.fromCell(formula);
      }
      catch ( ExitException e ) {
        throw new RuntimeException("NockFunction.fromCell:supplier", e);
      }
    };
    final NockExpressionNode expr = parseExpr(language, formula, 1L, true);
    final NockRootNode       root = new NockRootNode(language,
                                                     NockLanguage.DESCRIPTOR,
                                                     sourceSupplier, expr);
    return Truffle.getRuntime().createCallTarget(root);
  }

  public static RootCallTarget
    mappedTarget(NockLanguage language, SourceMappedNoun mapped)
      throws ExitException {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      return mapped;
    };
    final NockExpressionNode expr = parseExpr(language, mapped.noun, 1L, true);
    final NockRootNode       root = new NockRootNode(language,
                                                     NockLanguage.DESCRIPTOR,
                                                     sourceSupplier, expr);
    return Truffle.getRuntime().createCallTarget(root);
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return NockFunctionMessageResolutionForeign.ACCESS;
  }

  private static NockExpressionNode
    parseCons(NockLanguage language, Object head, Object tail, Object axis)
      throws ExitException {
    NockExpressionNode headNode =
      parseExpr(language, head, peg(axis, 2L), false);
    NockExpressionNode tailNode =
      parseExpr(language, tail, peg(axis, 3L), false);

    return ConsNodeGen.create(headNode, tailNode);
  }

  private static NockExpressionNode 
    parseSlot(Object axis)
      throws ExitException {
    Atom.require(axis);
    if ( axis instanceof Long ) {
      long l = (long) axis;
      if ( l < 2L ) {
        switch ( (int) l) {
          case 0:
            return new BailNode();
          case 1:
            return new IdentityNode();
        }
      }
    }
    return new SlotNode(axis);
  }

  private static NockExpressionNode 
    parseQuot(Object arg) {
    return ( arg instanceof Cell )
      ? new LiteralCellNode((Cell) arg)
      : ( arg instanceof BigAtom )
      ? new LiteralBigAtomNode((BigAtom) arg)
      : new LiteralLongNode((long) arg);
  }

  private static NockExpressionNode
    parseEval(NockLanguage language, Object arg, Object axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);

    NockExpressionNode subject = 
      parseExpr(language, args.head, peg(axis, 6L), false); 
    NockExpressionNode formula =
      parseExpr(language, args.tail, peg(axis, 7L), false); 
    NockFunctionLookupNode lookup =
      NockFunctionLookupNodeGen.create(formula, language.getContextReference());
    NockEvalNode eval = new NockEvalNode(lookup, subject);

    return tail
      ? new NockTailCallNode(eval)
      : new NockHeadCallNode(eval);
  }

  private static NockExpressionNode
    parseUnary(NockLanguage language, Object arg, Object axis)
      throws ExitException {
    return parseExpr(language, arg, peg(axis, 3L), false);
  }

  private static NockExpressionNode 
    parseIf(NockLanguage language, Object arg, Object axis, boolean tail)
      throws ExitException {
    Trel args = Trel.require(arg);
    return new IfNode(parseExpr(language, args.p, peg(axis, 6L), false),
                      parseExpr(language, args.q, peg(axis, 14L), tail),
                      parseExpr(language, args.r, peg(axis, 15L), tail));
  }

  private static NockExpressionNode 
    parseComp(NockLanguage language, Object arg, Object axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    return new ComposeNode(parseExpr(language, args.head, peg(axis, 6L), false),
                           parseExpr(language, args.tail, peg(axis, 7L), tail));
  }

  private static NockExpressionNode 
    parsePush(NockLanguage language, Object arg, Object axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    return new PushNode(parseExpr(language, args.head, peg(axis, 6L), false),
                        parseExpr(language, args.tail, peg(axis, 7L), tail));
  }

  private static NockExpressionNode
    parsePull(NockLanguage language, Object arg, Object axis, boolean tail)
      throws ExitException {
    Cell args = Cell.require(arg);
    Object armAxis = Atom.require(args.head);
    Object coreAxis = peg(axis, 7L);

    NockExpressionNode coreNode =
      parseExpr(language, args.tail, coreAxis, false);

    if ( Axis.subAxis(armAxis, 2L) ) {
      NockCallLookupNode pull = PullNodeGen.create(coreNode,
          armAxis, language.getContextReference());

      return tail
        ? new NockTailCallNode(pull)
        : new NockHeadCallNode(pull);
    }
    else {
      // Only pulls out of the battery of a core are treated as method calls,
      // pulls out of the payload get rewritten to an eval.
      NockExpressionNode subject = new IdentityNode();
      subject.setAxisInFormula(coreAxis);

      NockExpressionNode formula = parseSlot(armAxis);
      formula.setAxisInFormula(peg(axis, 6L));

      NockFunctionLookupNode lookup = NockFunctionLookupNodeGen.create(formula,
          language.getContextReference());

      NockCallLookupNode eval = new NockEvalNode(lookup, subject);
      NockExpressionNode call = tail
                              ? new NockHeadCallNode(eval)
                              : new NockTailCallNode(eval);
      call.setAxisInFormula(axis);
      return new ComposeNode(coreNode, call);
    }
  }

  /*
  private static ExpressionNode parseHint(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    Object hint = args.head;
    Object next = args.tail;

    if ( hint instanceof Cell ) {
      Cell hints = Cell.require(hint);
      ExpressionNode clue = parseExpr(hints.tail, false);
      switch ( hints.head ) {

        case Motes.MEAN:
        case Motes.HUNK:
        case Motes.LOSE:
        case Motes.SPOT:
          return StackNode.create(hints.head, clue, parseExpr(next, false));

        case Motes.FAST:
          return FastNode.create(clue, parseExpr(next, false));

        case Motes.SLOG:
          return SlogNode.create(clue, parseExpr(next, tail));

        case Motes.MEMO:
          return MemoNode.create(clue, parseExpr(next, false));

        default:
          return TossNode.create(clue, parseExpr(next, tail));
      }
    }
    else {
      // no recognized static hints
      return parseExpr(next, tail);
    }
  }

  private static WishNode parseWish(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    ExpressionNode ref = parseExpr(args.head, false);
    ExpressionNode gof = parseExpr(args.tail, false);

    return tail
      ? TailWishNode.create(ref, gof)
      : HeadWishNode.create(ref, gof);
  }

  private static ExpressionNode parseEdit(Object arg) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    Cell spec = Cell.require(args.head);
    ExpressionNode small = parseExpr(spec.tail, false);
    ExpressionNode large = parseExpr(args.tail, false);
    ExpressionNode chain = EditTermNode.create(small);

    ArrayDeque<Fragment> frags = new ArrayDeque<Fragment>();
    for ( Fragment f : Axis.require(spec.head) ) {
      frags.push(f);
    }

    while ( !frags.isEmpty() ) {
      chain = ( frags.pop() == Fragment.HEAD )
            ? EditHeadNode.create(chain)
            : EditTailNode.create(chain);
    }

    return EditNode.create(large, chain);
  }
*/

  private static NockExpressionNode 
    parseExpr(NockLanguage language, Object formula, Object axis, boolean tail)
      throws ExitException {
    Cell c = Cell.require(formula);
    Object op   = c.head,
           arg  = c.tail;

    NockExpressionNode node;

    if ( op instanceof Cell ) {
      node = parseCons(language, op, arg, axis);
    }
    else {
      int code = Atom.requireInt(op);
      switch ( code ) {
        case 0:
          node = parseSlot(arg);
          break;
        case 1:
          node = parseQuot(arg);
          break;
        case 2:
          node = parseEval(language, arg, axis, tail);
          break;
        case 3:
          node = DeepNodeGen.create(parseUnary(language, arg, axis));
          break;
        case 4:
          node = BumpNodeGen.create(parseUnary(language, arg, axis));
          break;
        case 5:
          node = SameNodeGen.create(parseUnary(language, arg, axis));
          break;
        case 6:
          node = parseIf(language, arg, axis, tail);
          break;
        case 7:
          node = parseComp(language, arg, axis, tail);
          break;
        case 8:
          node = parsePush(language, arg, axis, tail);
          break;
        case 9:
          node = parsePull(language, arg, axis, tail);
          break;
          /*
             case 10:
             return parseHint(arg);
             case 11:
             return parseWish(arg);
             case 12:
             return parseEdit(arg);
           */
        default:
          throw new ExitException("bad opcode");
      }
    }
    node.setAxisInFormula(axis);
    return node;
  }
}
