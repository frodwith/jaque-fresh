package net.frodwith.jaque.data;

import java.util.Map;
import java.util.HashMap;

import com.oracle.truffle.api.CompilerDirectives;      

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.Cords;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.FragHook;
import net.frodwith.jaque.dashboard.PullHook;

public final class FastClue {
  public final String name;
  public final Axis toParent;
  public final Map<String, Hook> hooks;
  public final Cell noun;

  private static final Cell CONSTANT_ZERO = new Cell(1L, 0L);
  private static final Cell CONSTANT_FRAG = new Cell(0L, 1L);

  public FastClue(Cell noun, String name, Axis toParent, Map<String,Hook> hooks) {
    this.noun = noun;
    this.name = name;
    this.toParent = toParent;
    this.hooks = hooks;
  }

  private static String chum(Object noun) throws ExitException {
    if ( noun instanceof Cell ) {
      Cell c = ((Cell) noun);
      int small = Atom.requireInt(c.tail);
      String cord = Cords.toString(c.head);

      return String.format("%s%d", cord, small);
    }
    else {
      return Cords.toString(noun);
    }
  }

  private static Object skipHints(Object formula) throws ExitException {
    while ( true ) {
      if ( formula instanceof Cell ) {
        Cell c = ((Cell) formula);
        if ( Equality.equals(10L, c.head) ) {
          formula = Cell.require(c.tail).tail;
          continue;
        }
      }
      return formula;
    }
  }

	private static Axis parseToParent(Object noun) throws ExitException {
    Cell f = Cell.require(skipHints(noun));
		if ( Equality.equals(CONSTANT_ZERO, f) ) {
			return Axis.CRASH;
		}
    if ( !Equality.equals(0L, f.head) ) {
      CompilerDirectives.transferToInterpreter();
      throw new ExitException("Non-fragment parent axis");
    }
    Axis ax = Axis.require(f.tail);
		if ( ax.inHead() ) {
      CompilerDirectives.transferToInterpreter();
			throw new ExitException("parent axis in head");
		}
		return ax;
	}

  private static Map<String,Hook> parseHooks(Object noun) throws ExitException {
    final Map<String, Hook> map = new HashMap<>();
    
    for ( Object i : new List(noun) ) {
      Cell kv = Cell.require(i);
      String term = Cords.toString(Atom.require(kv.head));
      Cell nock = Cell.require(skipHints(kv.tail));
      Hook h;
      switch ( Atom.requireInt(nock.head) ) {
        default:
          CompilerDirectives.transferToInterpreter();
          throw new ExitException("bad hook nock");

        case 0:
          h = new FragHook(Axis.require(nock.tail));
          break;

        case 9: {
          Cell args = Cell.require(nock.tail);
          Cell cof  = Cell.require(args.tail);
          int cofh  = Atom.requireInt(cof.head);
          if ( 0 != cofh ) {
            CompilerDirectives.transferToInterpreter();
            throw new ExitException("bad pull hook");
          }
          h = new PullHook(
              Axis.require(cof.tail),
              Axis.require(args.head));
          break;
        }
      }
      map.put(term, h);
    }

    return map;
  }

  public boolean isRoot() {
    return toParent == Axis.CRASH;
  }

  public static FastClue parse(Object clue) throws ExitException {
    Trel trel = Trel.require(clue);
    try {
      return new FastClue(Cell.require(clue),
        chum(trel.p),
        parseToParent(trel.q),
        parseHooks(trel.r));
    }
    catch ( IllegalArgumentException e ) {
      CompilerDirectives.transferToInterpreter();
      throw new ExitException("bad clue");
    }
  }
}
