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
import net.frodwith.jaque.util.Path;
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
  private static final NounLibrary nouns = NounLibrary.getUncached();
  private final NockContext context;

  public FormulaParser(NockContext context) {
    this.context = context;
  }

  private static NockExpressionNode
    axe(AxisBuilder axis, NockExpressionNode node) {
    node.setAxisInFormula(axis.write());
    return node;
  }

  private NockExpressionNode
    parseCons(Object headNoun, Object tailNoun, AxisBuilder axis)
      throws ExitException {
    NockExpressionNode
      head = parse(headNoun, axis.head(), false),
      tail = parse(tailNoun, axis.tail(), false);

    return axe(axis, ConsNodeGen.create(head, tail));
  }

  private NockExpressionNode parseSlot(Object arg) throws ExitException {
    return !nouns.fitsInBoolean(arg)
      ? SlotNode.fromPath(nouns.axisPath(arg))
      : nouns.asBoolean(arg)
      ? new BailNode()
      : new IdentityNode();
  }

  private static NockExpressionNode parseQuot(Object arg) {
    if ( nouns.isCell(arg) ) {
      return new LiteralCellNode(context.internCell(arg));
    }
    else if ( nouns.fitsInBoolean(arg) ) {
      // literal booleans are often the result of predicate branches, and should
      // be very slightly more efficient in that position 
      return new LiteralBooleanNode(nouns.asBoolean(arg));
    }
    // we could do the in-between sizes, but since noun references always need
    // to be Object (which can just hold a long), it doesn't really matter
    // and would just lead to a lot of unneccessary polymorphism.
    else if ( nouns.fitsInLong(arg) ) {
      return new LiteralLongNode(nouns.asLong(arg));
    }
    else {
      return new LiteralBigAtomNode(context.internAtom(arg));
    }
  }

  private NockExpressionNode
    parseEval(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    NockExpressionNode
      subject = parse(nouns.head(args), axis.tail().head(), false),
      formula = parse(nouns.tail(args), axis.tail().tail(), false);

    NockFunctionLookupNode 
      lookup = NockFunctionLookupNodeGen.create(formula);

    NockEvalNode eval = new NockEvalNode(lookup, subject);
    NockCallNode call = tail
      ? new NockTailCallNode(eval)
      : new NockHeadCallNode(eval);

    return axe(axis, call);
  }

  private NockExpressionNode
    parseUnary(Object arg, AxisBuilder axis)
      throws ExitException {
    return parse(arg, axis.tail(), false);
  }

  private NockExpressionNode
    parseDeep(Object arg, AxisBuilder axis)
      throws ExitException {
    return axe(axis, DeepNodeGen.create(parseUnary(arg, axis)))
  }

  private NockExpressionNode
    parseBump(Object arg, AxisBuilder axis)
      throws ExitException {
    return axe(axis, BumpNodeGen.create(parseUnary(arg, axis)));
  }

  private NockExpressionNode
    parseSame(Object args, AxisBuilder axis)
      throws ExitException {
    NockExpressionNode
      left = parse(nouns.head(args), axis.tail().head(), false),
      right = parse(nouns.tail(args), axis.tail().tail(), false);

    return axe(axis, SameNodeGen.create(left, right));
  }

  private NockExpressionNode
    parseIf(Object arg, AxisBuilder axis, boolean tail)
      throws ExitException {
    NockExpressionNode yes, no,
      test = parse(nouns.head(arg), axis.tail().head(), false);

    Object branches = nouns.tail(arg);
    ExitException yex = null, nex = null;

    try {
      yes = parse(nouns.head(branches), axis.tail().tail().head(), tail);
    }
    catch ( ExitException e ) {
      yex = e;
      yes = new BailNode();
    }
    try {
      no  = parse(nouns.tail(branches), axis.tail().tail().tail(), tail);
    }
    catch ( ExitException e) {
      nex = e;
      no  = new BailNode();
    }

    if ( null != yex && null != nex ) {
      throw new ExitException(yex.getMessage() + " and " + nex.getMessage());
    }
    else {
      return new IfNode(test, yes, no);
    }
  }

  private NockExpressionNode
    parseComp(Object args, AxisBuilder axis, boolean tail)
      throws ExitException {
    NockExpressionNode
      f = parse(nouns.head(args), axis.tail().head(), false),
      g = parse(nouns.tail(args), axis.tail().tail(), tail);

    return axe(axis, new ComposeNode(f, g));
  }

  private NockExpressionNode
    parsePush(Object args, AxisBuilder axis, boolean tail)
      throws ExitException {
    NockExpressionNode
      f = parse(nouns.head(args), axis.tail().head(), false),
      g = parse(nouns.tail(args), axis.tail().tail(), tail);
    return axe(axis, new PushNode(f, g));
  }

  private NockExpressionNode
    parsePull(Object args, AxisBuilder axis, boolean tail)
      throws ExitException {
    Object armAxis = nouns.head(args);
    AxisBuilder coreAxis = axis.tail().tail();

    NockExpressionNode core = parse(args.tail, coreAxis, false);

    if ( nouns.axisInHead(armAxis) ) {
      Path path = nouns.axisPath(armAxis);
      NockCallLookupNode pull = PullNodeGen.create(core, path);
      return axe(axis, tail
        ? new NockTailCallNode(pull)
        : new NockHeadCallNode(pull));
    }
    else {
      // Only pulls out of the battery of a core are treated as method calls,
      // pulls out of the payload get rewritten to an eval.
      NockExpressionNode
        subject = axe(coreAxis, new IdentityNode()),
        formula = axe(axis.tail().head(), parseSlot(armAxis));

      NockFunctionLookupNode
        lookup = NockFunctionLookupNodeGen.create(formula);

      NockCallLookupNode
        eval = new NockEvalNode(lookup, subject);

      NockExpressionNode
        call = axe(axis, tail
             ? new NockHeadCallNode(eval)
             : new NockTailCallNode(eval));

      return axe(axis, new ComposeNode(core, call));
    }
  }

  private NockExpressionNode
    parseStaticHint(Object tag, Object nextNoun,
      AxisBuilder axis, AxisBuilder nextAxis, boolean tail)
        throws ExitException {
    if ( nouns.fitsInInt(tag) ) {
      switch ( nouns.asInt(tag) ) {
        case Motes.CORE:
          return CoreNodeGen.create(parse(nextNoun, nextAxis, false));
      }
    }

    return parse(nextNoun, nextAxis, tail);
  }

  private NockExpressionNode parseDynamicHint(Object tag, Object clueFormula,
    Object nextNoun, AxisBuilder axis, AxisBuilder nextAxis, boolean tail)
      throws ExitException {
    NockExpressionNode nextNode,
      clueNode = parse(clueFormula, axis.tail().head(), false);

    if ( nouns.isCell(tag) ) {
      throw new ExitException("bad dynamic hint tag");
    }
    else if ( nouns.fitsInInt(tag) ) {
      switch ( nouns.asInt(tag) ) {
        case Motes.MEMO: {
          nextNode = parse(nextNoun, nextAxis, false);
          ConstantCell key = context.internCell(nextNoun);
          return axe(axis, MemoNodeGen.create(clueNode, nextNode, key);
        }

        case Motes.FAST: {
          nextNode = parse(nextNoun, nextAxis, false);
          return axe(axis, context.useFastHints()
            ? FastNodeGen.create(clueNode, nextNode)
            : new TossNode(clueNode, CoreNodeGen.create(next));
        }

        /*
        case Motes.MEAN:
        case Motes.HUNK:
        case Motes.LOSE:
        case Motes.SPOT:
          return StackNode.create(hints.head, clue, parse(next, false));

        case Motes.SLOG:
          return SlogNode.create(clue, parse(next, tail));
        */
      }
    }
    nextNode = parse(nextNoun, nextAxis, tail);
    return axe(axis, new TossNode(clueNode, nextNode));
  }

  private NockExpressionNode
    parseHint(Object args, AxisBuilder axis, boolean tail)
      throws ExitException {
    Object hint = nouns.head(args),
           next = nouns.tail(args);
    AxisBuilder nextAxis = axis.tail().tail();

    if ( nouns.isAtom(hint) ) {
      return parseStaticHint(hint, next, axis, nextAxis, tail);
    }
    else {
      Object tag = nouns.head(hint),
             clue = nouns.tail(hint);
      return parseDynamicHint(tag, clue, next, axis, nextAxis, tail);
    }
  }

  /*

  private static WishNode parseWish(Object arg, boolean tail) throws ShapeException, FormulaRequiredException {
    Cell args = Cell.require(arg);
    ExpressionNode ref = parse(args.head, false);
    ExpressionNode gof = parse(args.tail, false);

    return tail
      ? TailWishNode.create(ref, gof)
      : HeadWishNode.create(ref, gof);
  }

*/

  private NockExpressionNode
    parseEdit(Object args, AxisBuilder axis, boolean tail)
      throws ExitException {
    Object spec = nouns.head(args),
           exis = nouns.head(spec);
    NockExpressionNode
      small = parse(nouns.tail(spec), axis.tail().head().tail(), false),
      large = parse(nouns.tail(args), axis.tail().tail(), false);

    if ( nouns.fitsInBoolean(exis) ) {
      if ( nouns.asBoolean(exis) ) {
        // Editing axis 0 should rightly be regarded as a crash node like [0 0]
        return axe(axis, new BailNode());
      }
      else {
        // NockEditNode specializes to producing a cell, but edit 1 is valid
        // and could produce an atom.
        return axe(axis, new TossNode(large, smallc));
      }
    }
    else {
      ArrayDeque<Boolean> frags = new ArrayDeque<>();
      for ( boolean f : nouns.axisPath(exis) ) {
        frags.push(f);
      }
      EditPartNode chain = new EditTermNode(small);
      while ( !frags.isEmpty() ) {
        chain = frags.pop()
              ? new EditTailNode(chain)
              : new EditHeadNode(chain);
      }
      return axe(axis, new NockEditNode(large, chain, exis));
    }
  }

  @TruffleBoundary
  public NockExpressionNode
    parse(Object formula, AxisBuilder axis, boolean tail)
      throws ExitException {
    CompilerAsserts.neverPartOfCompilation();
    Object op   = nouns.head(formula),
           arg  = nouns.tail(formula);

    if ( nouns.isCell(op) ) {
      return parseCons(op, arg, axis);
    }
    else switch ( nouns.asInt(op) ) {
      case 0:
        return axe(axis,parseSlot(arg));
      case 1:
        return axe(axis,parseQuot(arg));
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
      // case 12:
      //   return parseWish(arg);
      default:
        throw new ExitException("invalid formula");
    }
  }

  @TruffleBoundary
  public NockExpressionNode parseRoot(Object formula) {
    return parse(formula, AxisBuilder.EMPTY, true);
  }
}
