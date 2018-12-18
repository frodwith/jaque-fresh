package net.frodwith.jaque.parser;

import java.util.ArrayDeque;
import java.util.function.Supplier;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.data.SourceMappedNoun;
import net.frodwith.jaque.nodes.*;
import net.frodwith.jaque.exception.ExitException;

public final class FormulaParser {
  private final NockLanguage language;

  public FormulaParser(NockLanguage language) {
    this.language = language;
  }

  private NockExpressionNode
    parseCons(Object head, Object tail, Axis axis) throws ExitException {
    NockExpressionNode headNode = parseExpr(head, axis.peg(2), false);
    NockExpressionNode tailNode = parseExpr(tail, axis.peg(3), false);

    return ConsNodeGen.create(headNode, tailNode);
  }

  private NockExpressionNode parseSlot(Axis axis) {
    if ( axis.isCrash() ) {
      return new BailNode();
    }
    else if ( axis == Axis.IDENTITY ) {
      return new IdentityNode();
    }
    else {
      return new SlotNode(axis);
    }
  }

  private NockExpressionNode parseQuot(Object arg) {
    return ( arg instanceof Cell )
      ? new LiteralCellNode((Cell) arg)
      : ( arg instanceof BigAtom )
      ? new LiteralBigAtomNode((BigAtom) arg)
      : new LiteralLongNode((long) arg);
  }

  private NockExpressionNode
    parseEval(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);

    NockExpressionNode subject = parseExpr(args.head, axis.peg(6), false);
    NockExpressionNode formula = parseExpr(args.tail, axis.peg(7), false);
    NockFunctionLookupNode lookup =
      NockFunctionLookupNodeGen.create(formula, language.getContextReference());
    NockEvalNode eval = new NockEvalNode(lookup, subject);

    return tail
      ? new NockTailCallNode(eval)
      : new NockHeadCallNode(eval);
  }

  private NockExpressionNode
    parseUnary(Object arg, Axis axis) throws ExitException {
    return parseExpr(arg, axis.peg(3), false);
  }

  private NockExpressionNode
    parseSame(Object arg, Axis axis) throws ExitException {
    Cell args = Cell.require(arg);
    NockExpressionNode left  = parseExpr(args.head, axis.peg(6), false);
    NockExpressionNode right = parseExpr(args.tail, axis.peg(7), false);
    return SameNodeGen.create(left, right);
  }

  private NockExpressionNode 
    parseIf(Object arg, Axis axis, boolean tail) throws ExitException {
    Trel args = Trel.require(arg);
    return new IfNode(parseExpr(args.p, axis.peg(6), false),
                      parseExpr(args.q, axis.peg(14), tail),
                      parseExpr(args.r, axis.peg(15), tail));
  }

  private NockExpressionNode 
    parseComp(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);
    return new ComposeNode(parseExpr(args.head, axis.peg(6), false),
                           parseExpr(args.tail, axis.peg(7), tail));
  }

  private NockExpressionNode 
    parsePush(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);
    return new PushNode(parseExpr(args.head, axis.peg(6), false),
                        parseExpr(args.tail, axis.peg(7), tail));
  }

  private NockExpressionNode
    parsePull(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);
    Axis armAxis = Axis.require(args.head);
    Axis coreAxis = axis.peg(7);

    NockExpressionNode coreNode = parseExpr(args.tail, coreAxis, false);

    if ( armAxis.inHead() ) {
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
      formula.setAxisInFormula(axis.peg(6));

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

  private NockExpressionNode
    parseHint(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);
    Object hint = args.head;
    Object next = args.tail;
    Axis nextAxis = axis.peg(7);

    if ( !(hint instanceof Cell) ) {
      // no recognized static hints
      return parseExpr(next, nextAxis, tail);
    }
    else {
      Cell hints = Cell.require(hint);
      
      NockExpressionNode clue = parseExpr(hints.tail, axis.peg(6), false);
      int tag;
      try {
        // all currently recognized hint tags fit into an int mote, which
        // is handy for switch statements.
        tag = Atom.requireInt(hints.head);
      }
      catch ( ExitException e ) {
        return new TossNode(clue, parseExpr(next, nextAxis, tail));
      }
      switch ( tag ) {
        case Motes.FAST:
          return new FastNode(language.getContextReference(),
            clue, parseExpr(next, nextAxis, false));
        default:
          return new TossNode(clue, parseExpr(next, nextAxis, tail));
        /*
        case Motes.MEAN:
        case Motes.HUNK:
        case Motes.LOSE:
        case Motes.SPOT:
          return StackNode.create(hints.head, clue, parseExpr(next, false));


        case Motes.SLOG:
          return SlogNode.create(clue, parseExpr(next, tail));

        case Motes.MEMO:
          return MemoNode.create(clue, parseExpr(next, false));
          */
      }
    }
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

  private NockExpressionNode
    parseEdit(Object arg, Axis axis, boolean tail) throws ExitException {
    Cell args = Cell.require(arg);
    Cell spec = Cell.require(args.head);
    Axis editAxis = Axis.require(spec.head);
    NockExpressionNode small = parseExpr(spec.tail, axis.peg(13), false),
                       large = parseExpr(args.tail, axis.peg(7), false);
    
    if ( Axis.IDENTITY == editAxis ) {
      // NockEditNode specializes to producing a cell, but edit 1 is valid
      // and could produce an atom.
      return new TossNode(large, small);
    }
    else {
      EditPartNode chain = new EditTermNode(small);

      ArrayDeque<Axis.Fragment> frags = new ArrayDeque<>();
      for ( Axis.Fragment f : editAxis ) {
        frags.push(f);
      }

      while ( !frags.isEmpty() ) {
        chain = ( frags.pop() == Axis.Fragment.HEAD )
              ? new EditHeadNode(chain)
              : new EditTailNode(chain);
      }

      return new NockEditNode(large, chain, editAxis);
    }
  }

  private NockExpressionNode 
    parseExpr(Object formula, Axis axis, boolean tail) throws ExitException {
    Cell c = Cell.require(formula);
    Object op   = c.head,
           arg  = c.tail;

    NockExpressionNode node;

    if ( op instanceof Cell ) {
      node = parseCons(op, arg, axis);
    }
    else {
      int code = Atom.requireInt(op);
      switch ( code ) {
        case 0:
          node = parseSlot(Axis.require(arg));
          break;
        case 1:
          node = parseQuot(arg);
          break;
        case 2:
          node = parseEval(arg, axis, tail);
          break;
        case 3:
          node = DeepNodeGen.create(parseUnary(arg, axis));
          break;
        case 4:
          node = BumpNodeGen.create(parseUnary(arg, axis));
          break;
        case 5:
          node = parseSame(arg, axis);
          break;
        case 6:
          node = parseIf(arg, axis, tail);
          break;
        case 7:
          node = parseComp(arg, axis, tail);
          break;
        case 8:
          node = parsePush(arg, axis, tail);
          break;
        case 9:
          node = parsePull(arg, axis, tail);
          break;
        case 10:
          node = parseEdit(arg, axis, tail);
          break;
        case 11:
          node = parseHint(arg, axis, tail);
          break;
          /*
             case 12:
               return parseWish(arg);
           */
        default:
          throw new ExitException("bad opcode");
      }
    }
    node.setAxisInFormula(axis);
    return node;
  }

  public NockExpressionNode parse(Object formula) throws ExitException {
    return parseExpr(formula, Axis.IDENTITY, true);
  }
  
  public RootCallTarget
    cellTarget(Cell formula) throws ExitException {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      try {
        return SourceMappedNoun.fromCell(formula);
      }
      catch ( ExitException e ) {
        throw new RuntimeException("NockFunction.fromCell:supplier", e);
      }
    };
    return Truffle.getRuntime()
      .createCallTarget(new NockRootNode(language, NockLanguage.DESCRIPTOR, 
        sourceSupplier, parse(formula)));
  }

  public RootCallTarget
    mappedTarget(SourceMappedNoun mapped) throws ExitException {
    Supplier<SourceMappedNoun> sourceSupplier = () -> {
      return mapped;
    };
    return Truffle.getRuntime()
      .createCallTarget(new NockRootNode(language, NockLanguage.DESCRIPTOR,
        sourceSupplier, parse(mapped.noun)));
  }
}
