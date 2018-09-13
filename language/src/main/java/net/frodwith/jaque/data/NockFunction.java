package net.frodwith.jaque.data;

import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;

import net.frodwith.jaque.exception.RequireException;
import net.frodwith.jaque.exception.FormulaRequiredException;
import net.frodwith.jaque.exception.Fail;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.nodes.NockExpressionNode;
import net.frodwith.jaque.nodes.LiteralCellNode;
import net.frodwith.jaque.nodes.LiteralBigAtomNode;
import net.frodwith.jaque.nodes.LiteralLongNode;
import net.frodwith.jaque.nodes.NockRootNode;
import net.frodwith.jaque.runtime.Atom;

public final class NockFunction {
  public final RootCallTarget callTarget;

  private NockFunction(RootCallTarget callTarget) {
    this.callTarget = callTarget;
  }

  public static NockFunction fromCell(NockLanguage language,
                                      FrameDescriptor frameDescriptor,
                                      Cell formula)
      throws Fail {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      try {
        return SourceMappedNoun.fromCell(formula);
      }
      catch ( Fail e ) {
        throw new RuntimeException("NockFunction.fromCell:supplier", e);
      }
    };
    final NockExpressionNode expr = parseExpr(formula, 1L, true);
    final NockRootNode       root = new NockRootNode(language, frameDescriptor, sourceSupplier, expr);
    return new NockFunction(Truffle.getRuntime().createCallTarget(root));
  }

  public static NockFunction fromMapped(NockLanguage language,
                                        FrameDescriptor frameDescriptor,
                                        SourceMappedNoun mapped)
      throws FormulaRequiredException {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      return mapped;
    };
    final NockExpressionNode expr = parseExpr(mapped.noun, 1L, true);
    final NockRootNode       root = new NockRootNode(language, frameDescriptor, sourceSupplier, expr);
    return new NockFunction(Truffle.getRuntime().createCallTarget(root));
  }

  /*
  private static ConsNode parseCons(Object head, Object tail) throws FormulaRequiredException {
    return ConsNodeGen.create(parseExpr(head, false), parseExpr(tail, false));
  }

  private static FragmentNode parseFrag(Object axis) throws ShapeException {
    ArrayDeque<FragmentPartNode> parts = new ArrayDeque<FragmentPartNode>();
    for ( Fragment f : Axis.require(axis) ) {
      FragmentPartNode node = ( f == Fragment.HEAD )
                            ? HeadNode.create()
                            : TailNode.create();
      parts.add(node);
    }
    FragmentPartNode[] children = new FragmentPartNode[parts.size()];
    return FragmentNode.create(parts.toArray(children));
  }
  */

  private static NockExpressionNode parseQuot(Object arg) {
    return ( arg instanceof Cell )
      ? new LiteralCellNode((Cell) arg)
      : ( arg instanceof BigAtom )
      ? new LiteralBigAtomNode((BigAtom) arg)
      : new LiteralLongNode((long) arg);
  }

  /*
  private static EvalNode parseEval(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    ExpressionNode subject = parseExpr(args.head, false); 
    ExpressionNode formula = parseExpr(args.head, false); 
    return tail
      ? TailEvalNode.create(subject, formula)
      : HeadEvalNode.create(subject, formula);
  }

  private static DeepNode parseDeep(Object arg) throws FormulaRequiredException {
    return DeepNode.create(parseExpr(arg, false));
  }

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

  private static NockExpressionNode parseExpr(Object formula, Object axis, boolean tail) throws FormulaRequiredException {
    try {
      Cell c = Cell.require(formula);
      Object op   = c.head,
             arg  = c.tail;

      NockExpressionNode node;

      if ( op instanceof Cell ) {
        // do nothing yet
        node = null;
      }
      else {
        int code = Atom.requireInt(op);
        switch ( code ) {
          /*
          case 0:
            return parseFrag(arg);
            */
          case 1:
            node = parseQuot(arg);
            break;
            /*
          case 2:
            return parseEval(arg);
          case 3:
            return parseDeep(arg);
          case 4:
            return parseBump(arg);
          case 5:
            return parseSame(arg);
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
