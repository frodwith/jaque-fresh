package net.frodwith.jaque.runtime;

import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.SourceMappedNoun;

import net.frodwith.jaque.exception.AtomRequiredException;
import net.frodwith.jaque.exception.RequireException;
import net.frodwith.jaque.exception.FormulaRequiredException;
import net.frodwith.jaque.exception.Fail;

import net.frodwith.jaque.nodes.*;
import net.frodwith.jaque.nodes.call.*;

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

  public static RootCallTarget cellTarget(NockLanguage language, Cell formula) throws Fail {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      try {
        return SourceMappedNoun.fromCell(formula);
      }
      catch ( Fail e ) {
        throw new RuntimeException("NockFunction.fromCell:supplier", e);
      }
    };
    final NockExpressionNode expr = parseExpr(language, formula, 1L, true);
    final NockRootNode       root = new NockRootNode(language,
                                                     NockLanguage.DESCRIPTOR,
                                                     sourceSupplier, expr);
    return Truffle.getRuntime().createCallTarget(root);
  }

  public static RootCallTarget mappedTarget(NockLanguage language, SourceMappedNoun mapped) throws Fail {
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
      throws Fail {
    NockExpressionNode headNode =
      parseExpr(language, head, HoonMath.peg(axis, 2L), false);
    NockExpressionNode tailNode =
      parseExpr(language, tail, HoonMath.peg(axis, 3L), false);

    return ConsNodeGen.create(headNode, tailNode);
  }

  private static NockExpressionNode 
    parseFrag(Object axis)
      throws AtomRequiredException {
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
    return FragmentNode.fromAxis(new Axis(axis));
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
      throws Fail {
    Cell args = Cell.require(arg);

    NockExpressionNode subject    = parseExpr(language, args.head, HoonMath.peg(axis, 6L), false); 
    NockExpressionNode formula    = parseExpr(language, args.tail, HoonMath.peg(axis, 7L), false); 
    NockFunctionLookupNode lookup = NockFunctionLookupNodeGen.create(formula, language.getContextReference());

    return tail
      ? new TailInvokeNode(lookup, subject)
      : new HeadInvokeNode(lookup, subject);
  }

  private static NockExpressionNode
    parseUnary(NockLanguage language, Object arg, Object axis)
      throws Fail {
    return parseExpr(language, arg, HoonMath.peg(axis, 3L), false);
  }

  /*
  private static BumpNode parseBump(Object arg) throws FormulaRequiredException {
    return BumpNode.create(parseExpr(arg, false));
  }

  private static SameNode parseSame(Object arg) throws FormulaRequiredException {
    return SameNode.create(parseExpr(arg, false));
  }

  private static IfNode parseCond(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Trel args = Trel.require(arg);

    return IfNode.create(parseExpr(args.p, false),
                         parseExpr(args.q, tail),
                         parseExpr(args.r, tail));
  }

  private static ComposeNode parseComp(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    return ComposeNode.create(parseExpr(args.head, false), parseExpr(args.tail, tail));
  }

  private static PushNode parsePush(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    return PushNode.create(parseExpr(args.head, false), parseExpr(args.tail, tail));
  }

  private static CallNode parsePull(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    Object axis = Atom.require(args.head);
    ExpressionNode core = parseExpr(args.tail, false);

    return tail
      ? TailCallNode.create(axis, core)
      : HeadCallNode.create(axis, core);
  }

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
    parseExpr(NockLanguage language, 
              Object formula, 
              Object axis,
              boolean tail) throws Fail {
    try {
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
            node = parseFrag(arg);
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
            /*
          case 6:
            return parseCond(arg);
          case 7:
            return parseComp(arg);
          case 8:
            return parsePush(arg);
          case 9:
            return parsePull(arg);
          case 10:
            return parseHint(arg);
          case 11:
            return parseWish(arg);
          case 12:
            return parseEdit(arg);
            */
          default:
            throw new FormulaRequiredException(op);
        }
      }
      node.setAxisInFormula(axis);
      return node;
    }
    catch ( RequireException e ) {
      throw new FormulaRequiredException(formula, e);
    }
  }
}
