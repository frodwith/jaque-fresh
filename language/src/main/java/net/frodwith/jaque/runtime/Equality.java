package net.frodwith.jaque.runtime;

import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;

public final class Equality {

  private static abstract class ComparisonStep {
    public abstract boolean execute(ArrayDeque<ComparisonStep> stack);

    public boolean executeOnce(ArrayDeque<ComparisonStep> stack, Set<CompareNouns> done) {
      return execute(stack);
    }
  }

  private static final class CompareNouns extends ComparisonStep {
    private final Object a, b;

    public CompareNouns(Object a, Object b) {
      this.a = a;
      this.b = b;
    }

    public int hashCode() {
      return Objects.hash(a, b);
    }

    public boolean equals(Object o) {
      if ( o instanceof CompareNouns ) {
        CompareNouns cn = (CompareNouns) o;
        return (a == cn.a) && (b == cn.b);
      }
      else {
        return false;
      }
    }

    @Override
    public boolean execute(ArrayDeque<ComparisonStep> stack) {
      if ( a == b ) {
        return true;
      }
      else {
        if ( a instanceof Cell ) {
          if ( b instanceof Cell ) {
            Cell ca = (Cell) a, 
                 cb = (Cell) b;
            if ( Cell.unequalMugs(ca, cb) ) {
              return false;
            }
            else {
              stack.push(new ComparedHeads(ca, cb));
              stack.push(new CompareNouns(ca.head, cb.head));
              return true;
            }
          }
          else {
            return false;
          }
        }
        else if ( b instanceof Cell ) {
          return false;
        }
        else {
          return Equality.equals(a, b);
        }
      }
    }

    @Override
    public boolean executeOnce(ArrayDeque<ComparisonStep> stack, Set<CompareNouns> done) {
      if ( done.contains(this) ) {
        return true;
      }
      else {
        done.add(this);
        return execute(stack);
      }
    }
  }

  private static final class ComparedHeads extends ComparisonStep {
    public final Cell a, b;

    public ComparedHeads(Cell a, Cell b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public boolean execute(ArrayDeque<ComparisonStep> stack) {
      a.unifyHeads(b);
      stack.push(new ComparedTails(a, b));
      stack.push(new CompareNouns(a.tail, b.tail));
      return true;
    }
  }

  private static final class ComparedTails extends ComparisonStep {
    public final Cell a, b;

    public ComparedTails(Cell a, Cell b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public boolean execute(ArrayDeque<ComparisonStep> stack) {
      a.unifyTails(b);
      a.unifyMeta(b);
      return true;
    }
  }

  public static boolean equals(BigAtom a, BigAtom b) {
    if ( a.unequalMugs(b) ) {
      return false;
    }
    else if ( Arrays.equals(a.words, b.words) ) {
      a.unify(b);
      return true;
    }
    else {
      return false;
    }
  }

  @TruffleBoundary
  private static boolean dedup(ArrayDeque<ComparisonStep> stack) {
    Set<CompareNouns> done = new HashSet<CompareNouns>();

    do {
      ComparisonStep cs = stack.pop();
      if ( !cs.executeOnce(stack, done) ) {
        return false;
      }
    } while ( !stack.isEmpty() );

    return true;
  }

  public static boolean equals(Cell a, Cell b) {
    ArrayDeque<ComparisonStep> stack = new ArrayDeque<ComparisonStep>();
    int whistle = 0;

    stack.push(new ComparedHeads(a, b));
    stack.push(new CompareNouns(a.head, b.head));

    do {
      if ( whistle++ > 65535 ) {
        return dedup(stack);
      }
      ComparisonStep cs = stack.pop();
      if ( !cs.execute(stack) ) {
        return false;
      }
    } while ( !stack.isEmpty() );

    return true;
  }

  public static boolean equals(Object a, Object b) {
    if ( a == b ) {
      return true;
    }
    else if ( a instanceof Cell ) {
      if ( b instanceof Cell ) {
        return equals((Cell) a, (Cell) b);
      }
      else {
        return false;
      }
    }
    else if ( b instanceof Cell ) {
      return false;
    }
    else if ( (a instanceof BigAtom) && (b instanceof BigAtom) ) {
      return equals((BigAtom) a, (BigAtom) b);
    }
    else if ( (a instanceof Long) && (b instanceof Long) ) {
      return a.equals(b);
    }
    else {
      return false;
    }
  }
}
